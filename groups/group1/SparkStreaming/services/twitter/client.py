import json
import requests
import sys
import socket
import os
import logging as log

ACCESS_TOKEN = os.environ['ACCESS_TOKEN']
ACCESS_SECRET = os.environ['ACCESS_SECRET']
CONSUMER_KEY = os.environ['CONSUMER_KEY']
CONSUMER_SECRET =  os.environ['CONSUMER_SECRET']
HEADERS = {
    "Content-type": "application/json",
    "Authorization": f"Bearer {os.environ['BEARER']}"
}

TWITTER_IP = "TWITTER_IP"
TWITTER_PORT = "TWITTER_PORT"


def create_headers(bearer_token):
    headers = {"Authorization": "Bearer {}".format(bearer_token)}
    return headers


def get_rules(headers):
    response = requests.get(
        "https://api.twitter.com/2/tweets/search/stream/rules", headers=headers
    )
    if response.status_code != 200:
        raise Exception(
            "Cannot get rules (HTTP {}): {}".format(response.status_code, response.text)
        )
    print(json.dumps(response.json()))
    return response.json()


def delete_all_rules(headers, rules):
    if rules is None or "data" not in rules:
        return None

    ids = list(map(lambda rule: rule["id"], rules["data"]))
    payload = {"delete": {"ids": ids}}
    response = requests.post(
        "https://api.twitter.com/2/tweets/search/stream/rules",
        headers=headers,
        json=payload
    )
    if response.status_code != 200:
        raise Exception(
            "Cannot delete rules (HTTP {}): {}".format(
                response.status_code, response.text
            )
        )
    print(json.dumps(response.json()))


def set_rules(headers):
    # You can adjust the rules if needed
    sample_rules = [
        {"value": "(covid OR brasil OR vacina OR governo) lang:pt", "tag": "politica"},
        {"value": "(bbb OR meme OR futebol OR tv) lang:pt", "tag": "entretenimento"},
    ]
    payload = {"add": sample_rules}
    response = requests.post(
        "https://api.twitter.com/2/tweets/search/stream/rules",
        headers=headers,
        json=payload,
    )
    if response.status_code != 201:
        raise Exception(
            "Cannot add rules (HTTP {}): {}".format(response.status_code, response.text)
        )
    print(json.dumps(response.json()))


def get_stream(ip, port, headers):

    TCP_IP = ip
    TCP_PORT = port
    conn = None
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    s.bind((TCP_IP, TCP_PORT))
    s.listen(1)

    log.info("Aguardando uma conexão TCP...")
    conn, addr = s.accept()

    log.info("Conectado... Começando a coletar tweets...")

    response = requests.get(
        "https://api.twitter.com/2/tweets/search/stream", headers=headers, stream=True,
    )
    print(response.status_code)
    if response.status_code != 200:
        raise Exception(
            "Cannot get stream (HTTP {}): {}".format(
                response.status_code, response.text
            )
        )
    for response_line in response.iter_lines():
        if response_line:
            try:
                full_tweet = json.loads(response_line)
                tweet_text = full_tweet['data']['text']
                log.debug("Tweet Text: " + tweet_text)
                log.debug("------------------------------------------")
                b = bytes(tweet_text + '\n', 'utf-8')
                conn.send(b)
            except Exception as e:
                log.error(f"Error: {e}")

if __name__ == "__main__":
    rules = get_rules(HEADERS)
    delete = delete_all_rules(HEADERS, rules)
    set_rules(HEADERS)
    get_stream(ip=os.environ[TWITTER_IP], port=int(os.environ[TWITTER_PORT]), headers=HEADERS)