import os
from flask import Flask, jsonify, request
from flask import render_template
import ast

SAMPLE_HOST_IP = "SAMPLE_HOST_IP"
SAMPLE_HOST_PORT = "SAMPLE_HOST_PORT"
SAMPLE_HOST_NAME = "SAMPLE_HOST_NAME"

app = Flask(__name__)

labels_hashtag = []
values_hashtag = []

labels_word = []
values_word = []


@app.route("/")
def get_chart_page():
    global labels_hashtag, values_hashtag, labels_word, values_word

    labels_hashtag = []
    values_hashtag = []

    labels_word = []
    values_word = []

    return render_template('chart.html', values_hashtag=values_hashtag, labels_hashtag=labels_hashtag,
                            labels_word=labels_word, values_word=values_word)


@app.route('/refreshDataHashtag')
def refresh_graph_data_hashtag():
    global labels_hashtag, values_hashtag
    print("labels hashtag now: " + str(labels_hashtag))
    print("data hashtag now: " + str(values_hashtag))
    return jsonify(sLabel=labels_hashtag, sData=values_hashtag)


@app.route('/refreshDataWord')
def refresh_graph_data_word():
    global labels_word, values_word
    print("labels word now: " + str(labels_word))
    print("data word now: " + str(values_word))
    return jsonify(sLabel=labels_word, sData=values_word)


@app.route('/updateDataHashtag', methods=['POST'])
def update_data_hashtag():
    global labels_hashtag, values_hashtag
    if not request.form or 'data' not in request.form:
        return "error", 400
    labels_hashtag = ast.literal_eval(request.form['label'])
    values_hashtag = ast.literal_eval(request.form['data'])
    print("labels hashtag received: " + str(labels_hashtag))
    print("data hashtag received: " + str(values_hashtag))
    return "success", 201

@app.route('/updateDataWord', methods=['POST'])
def update_data_word():
    global labels_word, values_word
    if not request.form or 'data' not in request.form:
        return "error", 400
    labels_word = ast.literal_eval(request.form['label'])
    values_word = ast.literal_eval(request.form['data'])
    print("labels word received: " + str(labels_word))
    print("data word received: " + str(values_word))
    return "success", 201


if __name__ == "__main__":
    app.run(host=os.environ[SAMPLE_HOST_IP],
            port=int(os.environ[SAMPLE_HOST_PORT]))
