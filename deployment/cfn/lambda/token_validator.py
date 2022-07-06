import json
import logging
import os

import requests

log = logging.getLogger()
log.setLevel(logging.DEBUG)


def lambda_handler(event, context):
    log.debug("event: {}".format(event))

    request_id = event["requestContext"]["requestId"]
    log.info("request_id: {}".format(request_id))

    # Validate request
    validate(event)

    access_token = event["headers"]["token"]
    log.debug("access_token: {}".format(access_token))

    # Call Okta to validate token
    response_dict = validate_token(access_token)

    if response_dict.get("active"):
        username = ""
        if "username" in response_dict:
            username = response_dict["username"]
        
        account_name = ""
        if "sub" in response_dict:
            account_name = response_dict["sub"]

        log.info("username: {}, account_name: {}"
                 .format(username, account_name))
        return generate_policy(username, account_name, request_id, "Allow", "*")
    else:
        log.info("Invalid Token: {}".format(access_token))
        raise Exception("Unauthorized")


def validate(event):
    if "headers" in event:
        if "token" in event["headers"]:
            application_token = event["headers"]["token"]
            log.debug("application_token: {}".format(application_token))
            if not application_token:
                raise Exception("Invalid token")
    else:
        log.info("Token header is missing")
        raise Exception("Invalid token")
    log.info("Validation completed")


def validate_token(access_token):
    token_url = os.environ["OAUTH_TOKEN_INTROSPECT_URL"]
    client_secret = os.environ["OAUTH_CLIENT_SECRET"]
    log.info("token_url: {}, client_secret: {}"
             .format(token_url, client_secret))

    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "Authorization": f"Basic {client_secret}"
    }
    payload = f"token={access_token}"
    response = requests.post(url=token_url, data=payload, headers=headers)
    log.info("response: {}".format(response))
    response_dict = json.loads(response.text)
    log.info("response_dict: {}".format(response_dict))
    return response_dict


def generate_policy(username, account_name, request_id, effect, method_arn):
    policy_document = {
        "principalId": username,
        "policyDocument": {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Action": "execute-api:Invoke",
                    "Effect": effect,
                    "Resource": method_arn
                }
            ]
        },
        "context": {
            "username": username,
            "accountName": account_name,
            "requestId": request_id
        }
    }
    return policy_document
