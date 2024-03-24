from flask import Flask, render_template, request, jsonify
import os

app = Flask(__name__)

# 초기 데이터셋 파일 경로
DATA_FOLDER = "data"

# 초기 데이터셋 파일 목록을 불러오는 함수
def list_files():
    files = os.listdir(DATA_FOLDER)
    return [file for file in files if file.endswith('.txt')]

# 초기 데이터셋 로드
def load_dataset(filename):
    try:
        with open(os.path.join(DATA_FOLDER, filename), 'r') as file:
            data = file.readlines()
        return [line.strip() for line in data]
    except FileNotFoundError:
        return [""] * 81

# 초기 데이터셋 저장
def save_dataset(filename, data):
    with open(os.path.join(DATA_FOLDER, filename), 'w') as file:
        for line in data:
            file.write(line + '\n')

@app.route('/', methods=['GET', 'POST'])
def index():
    files = list_files()
    if request.method == 'POST':
        # 모든 셀의 내용을 받아서 저장
        data = [request.form['text' + str(i)] for i in range(81)]
        path = request.form['path']
        save_dataset(path, data)
        return render_template('index.html', initial_data=data, files=files)

    # 초기 데이터셋 로드
    initial_data = load_dataset("current_dataset.txt")

    print("on new rendere:",initial_data)
    return render_template('index.html', initial_data=initial_data, files=files)

@app.route('/load', methods=['GET'])
def load():
    filename = request.args.get('filename')
    data = load_dataset(filename)
    print("filename",filename,data)
    initial_data = load_dataset(filename)
    files = list_files()
    return jsonify(data)

if __name__ == '__main__':
    app.run(debug=True)
