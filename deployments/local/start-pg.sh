podman run \
    -it \
    --rm \
    -v postgresml_data:/var/lib/postgresql \
    -p 5432:5432 \
    -p 8000:8000 \
    ghcr.io/postgresml/postgresml:2.10.0 \
    sudo -u postgresml psql -d postgresml