import os, pathlib, mimetypes, json, re, csv
from typing import List

from dotenv import load_dotenv
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload
from google.auth.transport.requests import Request
from google_auth_oauthlib.flow import InstalledAppFlow

# from google.oauth2.service_account import Credentials
# Use this when using a service account that will be able to download file ids
# creds = Credentials.from_service_account_file(os.environ["GOOGLE_KEY_FILE"],scopes=SCOPES)
# drive = build("drive", "v3", credentials=creds, cache_discovery=False)
from google.oauth2.credentials import Credentials

load_dotenv()

IMAGE_EXTS = {".jpeg", ".jpg", ".png", ".webm"}
FILE_REGEX = re.compile(r"(\d{4}-\d{2}-\d{2})_(.+?)\.[^.]+$", re.I)
GOOGLE_FOLDER = os.environ["GOOGLE_FOLDER"]
GOOGLE_SECRETS_FILE = os.environ["GOOGLE_SECRETS_FILE"]
GOOGLE_TOKEN = ".token.json"
SCOPES = ["https://www.googleapis.com/auth/drive"]


def get_user_creds() -> Credentials:
    """
    Reuse token if it exists, otherwise run the browser sign-in
    """
    creds = None
    if os.path.exists(GOOGLE_TOKEN):
        creds = Credentials.from_authorized_user_file(GOOGLE_TOKEN, SCOPES)

    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                GOOGLE_SECRETS_FILE, SCOPES
            )
            creds = flow.run_local_server(port=0)
        with open(GOOGLE_TOKEN, "w") as fh:
            fh.write(creds.to_json())
    return creds


drive = build("drive", "v3", credentials=get_user_creds(), cache_discovery=False)


def ensure_drive_folder(name: str, parent_id: str) -> str:
    """
    Return ID of folder called `name` under `parent_id`
    creating it if it doesn’t already exist.
    """
    query = (
        f"'{parent_id}' in parents and "
        "mimeType = 'application/vnd.google-apps.folder' and trashed = false"
    )

    page_token = None
    while True:
        resp = (
            drive.files()
            .list(
                q=query, fields="nextPageToken, files(id, name)", pageToken=page_token
            )
            .execute()
        )
        for f in resp.get("files", []):
            if f["name"] == name:
                return f["id"]

        page_token = resp.get("nextPageToken")
        if not page_token:
            # no more pages → folder not found
            break

    body = {
        "name": name,
        "parents": [parent_id],
        "mimeType": "application/vnd.google-apps.folder",
    }
    return drive.files().create(body=body, fields="id").execute()["id"]


def upload_file(local_path: pathlib.Path, parent_id: str) -> str:
    """
    Upload file and return ID.
    """
    q = f"'{parent_id}' in parents and name = '{local_path.name}' and trashed = false"
    hit = drive.files().list(q=q, fields="files(id)", pageSize=1).execute().get("files")
    if hit:
        return hit[0]["id"]

    mime_type, _ = mimetypes.guess_type(local_path)
    media = MediaFileUpload(
        local_path, mimetype=mime_type or "application/octet-stream", resumable=True
    )
    body = {"name": local_path.name, "parents": [parent_id]}
    file = drive.files().create(body=body, media_body=media, fields="id").execute()
    return file["id"]


def sync_and_generate_csv(libraries):
    rows = []

    for lib in libraries:
        lib_path = pathlib.Path(lib)
        print(f"> Syncing {lib_path}")
        for season in lib_path.iterdir():
            if not season.is_dir():
                continue

            drive_folder_id = ensure_drive_folder(season.name, GOOGLE_FOLDER)

            for f in season.iterdir():
                print(f" > Season {season.name}: {f}")
                if not f.is_file():
                    continue

                mg = FILE_REGEX.match(f.name)
                if mg is None:
                    raise ValueError(
                        f"{f.name!r} should look like YYYY-MM-DD_description.ext"
                    )

                date_str, description = mg.groups()
                drive_id = upload_file(f, drive_folder_id)

                ext = f.suffix.lower()
                ftype = "image" if ext in IMAGE_EXTS else "video"

                rows.append([season.name, date_str, drive_id, description, ftype])

    rows.sort(key=lambda r: r[1], reverse=True)
    with open("./data/gallery.csv", "w") as fh:
        csv.writer(fh).writerows(
            [["season", "date", "id", "description", "type"], *rows]
        )


if __name__ == "__main__":
    sync_and_generate_csv(os.environ["MEDIA_PATHS"].split(":"))
