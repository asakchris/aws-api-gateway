import json
import os

import requests


def lambda_handler(event, context):
  print(event)

  # Validate request
  validate(event)

  token = event['authorizationToken']
  response_dict = validate_token(token)

  if response_dict.get('active'):
    principal_id = response_dict['sub']
    print('create headers')
    print(principal_id)
    oauth_header = {}
    if 'sub' in response_dict:
      oauth_header['uid'] = response_dict['sub']

    if 'username' in response_dict:
      oauth_header['username'] = response_dict['username']

    print('OAuth Header: ' + json.dumps(oauth_header))
    print('Token validation success: ' + principal_id)
    return generatePolicy(principal_id, oauth_header, 'Allow', '*')
  else:
    print('Invalid Token')
    raise Exception('Unauthorized')


def validate(event):
  if 'authorizationToken' in event:
    application_token = event['authorizationToken']
    print('application_token: ', application_token)
    if not application_token:
      raise Exception('Invalid token')
  else:
    print('Token header is missing')
    raise Exception('Invalid token')


def validate_token(token):
  url = os.environ['OAUTH_TOKEN_INTROSPECT_URL']
  client_secret = os.environ['OAUTH_CLIENT_SECRET']
  headers = {'Content-Type': 'application/x-www-form-urlencoded',
             'Authorization': f'Basic {client_secret}'}
  payload = f'token={token}'
  response = requests.post(url=url, data=payload, headers=headers)
  response_dict = json.loads(response.text)
  print(response_dict)
  return response_dict


def generatePolicy(principal_id, oauth_header, effect, method_arn):
  policy_document = {
    'principalId': principal_id,
    'policyDocument': {
      'Version': '2012-10-17',
      'Statement': [
        {
          'Action': 'execute-api:Invoke',
          'Effect': effect,
          'Resource': method_arn
        }
      ]
    },
    'context': {
      'uid': principal_id,
      'idm_header': json.dumps(oauth_header)
    }
  }
  return policy_document
