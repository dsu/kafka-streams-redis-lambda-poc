FROM lambci/lambda:nodejs12.x

ENV AWS_LAMBDA_FUNCTION_NAME=docker-build \
    AWS_LAMBDA_FUNCTION_VERSION=2 \
    AWS_LAMBDA_FUNCTION_MEMORY_SIZE=384 \
    AWS_LAMBDA_FUNCTION_TIMEOUT=60 \
    AWS_REGION=us-east-1

WORKDIR /var/task
COPY . /var/task
USER root
RUN npm install
COPY . /var/task

# If we want to match permissions in /var/task exactly...
USER root
RUN chown -R slicer:497 .
USER sbx_user1051
