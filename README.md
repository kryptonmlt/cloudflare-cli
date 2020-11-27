# cloudflare-cli

An application that connects to the cloudflare api given an email / token / accountID.

## How to Run
- docker run -it -e EMAIL="YOUR_CLOUFLARE_EMAIL" -e KEY="YOUR_API_KEY" -e ID="YOUR_ACCOUNT_ID" kryptonmlt/cloudflare-cli
## Local Development
- Build
    - jdk 13+ installed
    - mvn clean install
- Run
    - docker-compose exec cloudflare-cli java -jar /root/cloudflare-cli
# Release
- docker build . -t kryptonmlt/cloudflare-cli
- docker push kryptonmlt/cloudflare-cli
- Docker registry: https://hub.docker.com/repository/docker/kryptonmlt/cloudflare-cli