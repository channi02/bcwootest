from flask import Flask, render_template, request

import os
import uuid

app = Flask(__name__)

data_file_path = 'data.txt'

def save_data(post_id, data):
    with open(data_file_path, 'a') as file:
        file.write(f"{post_id},{data}\n")

def load_data():
    try:
        with open(data_file_path, 'r') as file:
            lines = file.readlines()
            return [line.strip().split(',', 1) for line in lines]
    except FileNotFoundError:
        return []

def update_data(post_id, new_data):
    all_data = load_data()
    for i, (id, data) in enumerate(all_data):
        if id == post_id:
            all_data[i] = (id, new_data)
            break
    with open(data_file_path, 'w') as file:
        for id, data in all_data:
            file.write(f"{id},{data}\n")

def delete_data(post_id):
    all_data = load_data()
    all_data = [item for item in all_data if item[0] != post_id]
    with open(data_file_path, 'w') as file:
        for id, data in all_data:
            file.write(f"{id},{data}\n")

@app.route('/')
def index():
    posts = load_data()
    return render_template('index.html', posts=posts)

@app.route('/process', methods=['POST'])
def process():
    user_input = request.form['user_input']
    post_id = str(uuid.uuid4())
    save_data(post_id, user_input)
    posts = load_data()
    return render_template('index.html', posts=posts)

@app.route('/update/<post_id>', methods=['POST'])
def update(post_id):
    new_data = request.form['modified_data']
    update_data(post_id, new_data)
    posts = load_data()
    return render_template('index.html', posts=posts)

@app.route('/modify/<post_id>', methods=['GET'])
def modify(post_id):
    posts = load_data()
    selected_post = [data for data in posts if data[0] == post_id][0]
    return render_template('modify.html', post_id=post_id, post_content=selected_post[1])

@app.route('/delete/<post_id>')
def delete(post_id):
    delete_data(post_id)
    posts = load_data()
    return render_template('index.html', posts=posts)



if __name__ == '__main__':
    app.run(debug=True)
