import os
import pathlib
import mimetypes
import re
import csv

import boto3
from botocore.config import Config
from botocore.exceptions import ClientError
from dotenv import load_dotenv


load_dotenv()

IMAGE_EXTS = {".jpeg", ".jpg", ".png", ".webp"}
FILE_REGEX = re.compile(r"(\d{4}-\d{2}-\d{2})_(.+?)\.[^.]+$", re.I)

S3_BUCKET = os.environ["S3_BUCKET"]
S3_ACCESS_KEY_ID = os.environ["S3_ACCESS_KEY_ID"]
S3_SECRET_ACCESS_KEY = os.environ["S3_SECRET_ACCESS_KEY"]
S3_ENDPOINT = os.environ.get("S3_ENDPOINT")
S3_REGION = os.environ.get("S3_REGION", "us-east-1")


s3 = boto3.client(
    "s3",
    endpoint_url=S3_ENDPOINT,
    region_name=S3_REGION,
    aws_access_key_id=S3_ACCESS_KEY_ID,
    aws_secret_access_key=S3_SECRET_ACCESS_KEY,
    config=Config(
        signature_version="s3v4",
        s3={"addressing_style": "path"},
    ),
)


def object_exists(key: str) -> bool:
    """Return true if object already exists in remote storage."""
    try:
        s3.head_object(Bucket=S3_BUCKET, Key=key)
        return True
    except ClientError as e:
        if e.response["ResponseMetadata"]["HTTPStatusCode"] == 404:
            return False
        raise


def upload_file(local_path: pathlib.Path, key: str):
    """Upload file unless its object key already exists."""
    if object_exists(key):
        return

    mime_type, _ = mimetypes.guess_type(local_path)

    print(f"    Uploading -> {key}")

    s3.upload_file(
        str(local_path),
        S3_BUCKET,
        key,
        ExtraArgs={
            "ContentType": mime_type or "application/octet-stream",
        },
    )


def sync_and_generate_csv(libraries):
    rows = []

    for lib in libraries:
        lib_path = pathlib.Path(lib)
        print(f"> Syncing {lib_path}")

        for season in lib_path.iterdir():
            if not season.is_dir():
                continue

            for f in season.iterdir():
                if not f.is_file():
                    continue

                print(f" > Season {season.name}: {f}")

                match = FILE_REGEX.match(f.name)
                if match is None:
                    raise ValueError(
                        f"{f.name!r} should look like " "YYYY-MM-DD_description.ext"
                    )

                date_str, description = match.groups()

                # Object storage has no real directories.
                # "/" simply forms part of the object key.
                key = f"{season.name}/{f.name}"

                upload_file(f, key)

                ext = f.suffix.lower()
                ftype = "image" if ext in IMAGE_EXTS else "video"

                rows.append(
                    [
                        season.name,
                        date_str,
                        key,
                        description,
                        ftype,
                    ]
                )

    rows.sort(key=lambda row: row[1], reverse=True)

    with open("./data/gallery.csv", "w", newline="") as fh:
        csv.writer(fh).writerows(
            [
                ["season", "date", "id", "description", "type"],
                *rows,
            ]
        )


if __name__ == "__main__":
    sync_and_generate_csv(os.environ["MEDIA_PATHS"].split(":"))
