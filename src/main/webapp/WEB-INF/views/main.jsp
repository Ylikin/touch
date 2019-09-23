<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
    <style>
        .chatbox {
            display: none;
        }

        .messages {
            background-color: #369;
            width: 500px;
            padding: 20px;
        }

        .start .username {
            width: 420px;
            padding: 20px;
        }

        .messages .msg {
            background-color: #fff;
            border-radius: 10px;
            margin-bottom: 10px;
            overflow: hidden;
        }

        .messages .msg .from {
            background-color: #396;
            line-height: 30px;
            text-align: center;
            color: white;
        }

        .messages .msg .text {
            padding: 10px;
        }

        #start {
            position: relative;
            z-index: 0;
        }

        textarea.msg {
            width: 540px;
            padding: 10px;
            resize: none;
            border: none;
            box-shadow: 2px 2px 5px 0 inset;
        }

        input:invalid {
            /*border: 2px dashed red;*/
            border: 30px solid red;
            z-index: 9999;

            position: absolute;


        }

        input:valid .start #start {
            border: 2px solid black;
            display: block;
        }
    </style>
    <script>
        let ChatUnit = {
            init() {
                this.startbox = document.querySelector(".start");
                this.chatbox = document.querySelector(".chatbox");
                this.startBtn = this.startbox.querySelector("button");
                this.nameInput = this.startbox.querySelector("input");
                this.msgTextArea = document.querySelector("textarea");

                this.chatMessageContainer = this.chatbox.querySelector(".messages");
                this.bindEvents();
            },
            bindEvents() {
                // this.nameInput.addEventListener("input", function (event) {
                //     if (this.nameInput.validity.valid) {
                //         this.startBtn.style.display="block";
                //     }
                // });
                if (this.nameInput.checkValidity())
                    this.startBtn.style.display = "block";
                this.startBtn.addEventListener("click", e => this.openSocket());
                // this.nameInput.addEventListener("input", e => this.openSocket());
                this.msgTextArea.addEventListener("keyup", e => {
                    if (e.ctrlKey && e.keyCode === 13) {
                        e.preventDefault();
                        this.send();
                    }

                })
            },
            send() {
                this.sendMessage({
                    name: this.namein + " " + this.roleUser,
                    text: this.msgTextArea.value
                });
            },
            onOpenSock() {

            },
            onMessage(msg) {
                let msgBlock = document.createElement("div");
                msgBlock.className = "msg";
                let fromBlock = document.createElement("div");
                fromBlock.className = "from";
                fromBlock.innerText = msg.name;
                let textBlock = document.createElement("div");
                textBlock.className = "text";
                textBlock.innerText = msg.text;

                msgBlock.appendChild(fromBlock);
                msgBlock.appendChild(textBlock);
                this.chatMessageContainer.prepend(msgBlock);

            },
            onClose() {

            },
            sendMessage(msg) {
                this.onMessage({name: "I`m ", text: msg.text});
                this.msgTextArea.value = "";
                this.ws.send(JSON.stringify(msg));
            },
            openSocket() {

                this.name = this.nameInput.value;

                this.namein = this.name.split(" ")[2];
                this.roleUser = this.name.split(" ")[1];


                this.ws = new WebSocket("ws://localhost:8080/WebS_war/chat/" + this.namein + "_" + this.roleUser);
                this.ws.onopen = () => this.onOpenSock();
                this.ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
                this.ws.onclose = () => this.onClose();


                this.startbox.style.display = "none";
                this.chatbox.style.display = "block";


            }
        };

        window.addEventListener("load", e => ChatUnit.init());
    </script>

</head>
<body>
<h1>Support</h1>

<div class="start">
    <input type="text" class="username" placeholder="to start enter such command [ /create (client or agent) name] "
           required pattern="/create agent [a-z]+|/create client [a-z]+">
    <button id="start">start</button>
</div>
<div class="chatbox">
    <textarea class="msg">

        </textarea>
    <div class="messages">

    </div>

</div>
</body>
</html>
