let message = document.getElementById("message");
let submit = document.getElementById("submit");
let chatLog = document.getElementById("chatLog");
let resolved = document.getElementById("resolved");

function send(e) {
  e.preventDefault();

  let obj = {
    chat: chat.value,
  };

  fetch("http://localhost:8080/foodchat/chat", {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=utf-8",
    },
    body: JSON.stringify(obj),
  });

  let myMsgDiv =
    '<div class="myMsg"><span class="msg">' + chat.value + "</span></div>";
  chatLog.insertAdjacentHTML("beforeend", myMsgDiv);

  bot(e);
}

function bot(e) {
  e.preventDefault();

  fetch("http://localhost:8080/foodchat/chat")
    .then((resp) => resp.json())
    .then((data) => {
      let food = data.food;

      let botMsgDiv =
        '<div class="anotherMsg"><span class="msg">' + food + "</span></div>";
      chatLog.insertAdjacentHTML("beforeend", botMsgDiv);

      let resolve = data.resolve;
      resolved.innerHTML = resolve;
    });
}

window.addEventListener("load", function (e) {
  submit.addEventListener("click", send);
});
