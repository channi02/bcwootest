from flask import Flask, render_template, request

import re
import os
import uuid

app = Flask(__name__)

data_file_path = 'intro.txt'
data_folder = 'data'



def get_text_files():
    text_files = []
    for file in os.listdir(data_folder):
        if file.endswith('.txt'):
            text_files.append(file)
    return text_files

def update_file_path(new_path):
    global data_file_path
    data_file_path = new_path

def read_file(file_name):
    file_path = os.path.join(data_folder, file_name)
    with open(file_path, 'r') as file:
        return file.read()

def save_data(post_id, data):
    with open(data_file_path, 'a') as file:
        file.write(f"{post_id},{data}\n")

def load_data():
    try:
        with open(data_file_path, 'r') as file:
            print("sucess open ", data_file_path)
            lines = file.readlines()
            return [line.strip().split(',', 1) for line in lines]
    except FileNotFoundError:
        print("load data not found")
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

def add_data(post_id, amount):
    all_data = load_data()
    pattern = r'\((\d+)/(\d+)\)'
    for i, (id, data) in enumerate(all_data):
        if id == post_id:
            new_data = re.sub(pattern, lambda match: f'({int(match.group(1)) + amount}/{match.group(2)})', data)
            all_data[i] = (id, new_data)
            break

    with open(data_file_path, 'w') as file:
        for id, data in all_data:
            file.write(f"{id},{data}\n")

@app.route('/')
def index():
    posts = load_data()
    text_files = get_text_files()
    return render_template('index.html', posts=posts,text_files=text_files)

@app.route('/process', methods=['POST'])
def process():
    user_input = request.form['user_input']
    if user_input != "":
        print("add", user_input, ",")
        post_id = str(uuid.uuid4())
        save_data(post_id, user_input)

    selected_file=request.form['selected_file']
    print("selcted_file", selected_file)
    if selected_file != "nonselect":
        update_file_path(os.path.join(data_folder, selected_file))


    print(data_file_path)

            
    posts = load_data()
    text_files = get_text_files()
    return render_template('index.html', posts=posts, text_files=text_files) #,selected_file=selected_file)

@app.route('/update/<post_id>', methods=['POST'])
def update(post_id):
    new_data = request.form['modified_data']
    update_data(post_id, new_data)
    posts = load_data()
    text_files = get_text_files()
    return render_template('index.html', posts=posts, text_files=text_files)

@app.route('/modify/<post_id>', methods=['GET'])
def modify(post_id):
    posts = load_data()
    selected_post = [data for data in posts if data[0] == post_id][0]
    text_files = get_text_files()
    return render_template('modify.html', post_id=post_id, post_content=selected_post[1], text_files=text_files)


@app.route('/delete/<post_id>')
def delete(post_id):
    delete_data(post_id)
    posts = load_data()
    text_files = get_text_files()
    return render_template('index.html', posts=posts, text_files=text_files)

@app.route('/up/<post_id>')
def up(post_id):
    add_data(post_id,1)
    posts = load_data()
    text_files = get_text_files()
    return render_template('index.html', posts=posts, text_files=text_files)

@app.route('/down/<post_id>')
def down(post_id):
    add_data(post_id,-1)
    posts = load_data()
    text_files = get_text_files()
    return render_template('index.html', posts=posts,text_files=text_files)


if __name__ == '__main__':
    update_file_path(os.path.join(data_folder, data_file_path))
    app.run(debug=True)
