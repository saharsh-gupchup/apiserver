# Gupchup API Server

## Build

### Docker

        docker build -t gupchup-server gupchup-server

### Buildah

        buildah bud -t gupchup-server gupchup-server

## Run

### Docker

        docker run -d -name gupchup-server -p 8080:8080 gupchup-server

### Buildah and Podman

        sudo buildah bud -t gupchup-server gupchup-server
        sudo podman run -d -name gupchup-server -p 8080:8080 gupchup-server

