# cloudflare-cli

An application that allows you to interact with the cloudflare api given an email / token / accountID.

## How to Run
- **docker run -it -e EMAIL="YOUR_CLOUFLARE_EMAIL" -e KEY="YOUR_API_KEY" -e ID="YOUR_ACCOUNT_ID" kryptonmlt/cloudflare-cli**

## Cloudflare Tokens
Cloudflare CLI uses the Cloudflare API internally, there are 2 ways of gaining access to the API; 
[API Keys](https://developers.cloudflare.com/api/tokens) and [API Tokens](https://developers.cloudflare.com/api/keys).
Currently this cloudflare-cli uses the API Token method but API Keys will be implemented later on.
![Image on how to get your api token](docs/api_token.png)

## Configuration
- As explained in the *How to Run* section there is no configuration file that needs to be filled.Instead everything 
can be passed as environment variables in the docker run command.

## Usage
As with most CLIs, *help* is your friend here
![Help Command in cloudflare-cli](docs/help.png)
You can also run help on a particular command to get more details; example *help single-update*
![Help Command in cloudflare-cli](docs/help-1.png)

## Examples
- Create an unproxied dns record
    - create example.com A test.example.com 127.0.0.1 false 
- Update a dns record to a CNAME
    - single-update example.com CNAME test www.example.com true true
- list all zones
    - zone
- find all dns records that have the letters *cdn*
    - find cdn

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