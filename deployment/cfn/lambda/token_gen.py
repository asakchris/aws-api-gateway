import json
import os
import sys

import requests

print('Loading token generator')


def lambda_handler(event, context):
  print('event: ', event)
  try:
    validate(event)

    # Call OAuth to generate token
    request_body = event['body']
    token_response = generate_token(request_body)
    print('token_response: ', token_response)

    return {
      'statusCode': token_response.status_code,
      'headers': {'Content-Type': 'application/json'},
      'body': token_response.text
    }
  except TokenException as auth_ex:
    msg = {'message': auth_ex.status_message}
    return {
      'statusCode': auth_ex.status_code,
      'headers': {'Content-Type': 'application/json'},
      'body': json.dumps(msg)
    }
  except:
    print('Unexpected error: ', sys.exc_info()[0])
    msg = {'message': 'Internal error during token generation'}
    return {
      'statusCode': 500,
      'headers': {'Content-Type': 'application/json'},
      'body': json.dumps(msg)
    }


def validate(event):
  h_content_type = event['headers']['Content-Type']
  if h_content_type != 'application/x-www-form-urlencoded':
    print('h_content_type: ', h_content_type)
    raise TokenException('Invalid Content-Type header', 401,
                         'Invalid Content-Type header')

  request_body = event['body']
  if not request_body:
    raise TokenException('Invalid request body', 401, 'Invalid request body')


def generate_token(request_body):
  token_url = get_env_variable('OAUTH_TOKEN_GEN_URL')
  client_secret = get_env_variable('OAUTH_CLIENT_SECRET')
  print('token_url: ', token_url, ', client_secret: ', client_secret)

  authorization = 'Basic ' + client_secret
  request_headers = {'Content-type': 'application/x-www-form-urlencoded',
                     'Authorization': authorization}
  payload = request_body + '&grant_type=password&scope=offline_access'

  response = requests.post(token_url, data=payload,
                           headers=request_headers)
  print('response: ', response)
  return response


def get_env_variable(var_name):
  try:
    return os.environ[var_name]
  except KeyError as err:
    print('Exception while reading env variable: ', var_name, ': ', err)
    raise err


class TokenException(Exception):
  '''Basic exception for errors raised during token generation'''

  def __init__(self, message, status_code, status_message):
    super().__init__(message)
    self.status_code = status_code
    self.status_message = status_message
