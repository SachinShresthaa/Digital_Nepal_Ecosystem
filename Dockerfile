# Dockerfile template for Digital Nepal Ecosystem backend
# Update this file with the correct runtime and startup command for your service.

FROM ubuntu:24.04 AS builder

WORKDIR /app

# Copy application source into the container.
COPY . /app

# Install dependencies for a known runtime below. Remove or update the section
# that does not match your backend stack.

# Node.js example:
# RUN apt-get update && apt-get install -y curl ca-certificates \
#     && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
#     && apt-get install -y nodejs \
#     && npm install \
#     && npm run build

# Python example:
# RUN apt-get update && apt-get install -y python3 python3-pip \
#     && pip install --no-cache-dir -r requirements.txt

# Java example:
# RUN apt-get update && apt-get install -y openjdk-21 maven \
#     && mvn package

FROM ubuntu:24.04
WORKDIR /app
COPY --from=builder /app /app

# Replace this with your actual runtime command.
CMD ["bash"]

