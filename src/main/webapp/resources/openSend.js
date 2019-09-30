let ChatUnit;
ChatUnit = {
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
    send: function () {
        this.sendMessage({
            name: this.namein,
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