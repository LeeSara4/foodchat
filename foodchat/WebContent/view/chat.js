let message = document.getElementById("message");
let submit = document.getElementById("submit");
let chatLog = document.getElementById("chatLog");
let resolved = document.getElementById("resolved");

function send(e) {
  if (e != null) {
    e.preventDefault();
  }

  let obj = {
    chat: chat.value,
  };

  let myMsgDiv =
    '<div class="myMsg"><span class="msg">' + chat.value + "</span></div>";
  chatLog.insertAdjacentHTML("beforeend", myMsgDiv);

   fetch("http://localhost:8080/foodchat/chat", {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=utf-8",
    },
    body: JSON.stringify(obj),
  })
    .then((resp) => resp.json())
    .then((data) => {
      let food = data.food;
      document.getElementById("chat").value = "";

      let botMsgDiv =
        '<div class="anotherMsg"><span class="msg">' + food + "</span></div>";
      chatLog.insertAdjacentHTML("beforeend", botMsgDiv);

      if (food === "그것은 무엇?") {
        addOptions(["사람", "날씨", "장소", "재료", "행동"]);
      } else if (food === "그것은 어떤음식과 매칭?") {
        addOptions(["떡볶이", "갈비탕", "돈가스", "죽"]);
      }

      let resolve = data.resolve;
      resolved.innerHTML = resolve;
    });
}

function addOptions(options) {
  const optionsElement = document.createElement("div");
  options.forEach((option) => {
    const button = document.createElement("button");
    button.textContent = option;
    button.onclick = () => handleOptionSelect(option);
    optionsElement.appendChild(button);
  });
  chatLog.appendChild(optionsElement);
}

function handleOptionSelect(option) {
  addMessage("user", option);

  fetch("http://localhost:8080/foodchat/chat", {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=utf-8" },
    body: JSON.stringify({ chat: option }),
  })
    .then((resp) => resp.json())
    .then((data) => {
      let food = data.food;

      let botMsgDiv =
        '<div class="anotherMsg"><span class="msg">' + food + "</span></div>";
      chatLog.insertAdjacentHTML("beforeend", botMsgDiv);

      if (food === "그것은 무엇?") {
        addOptions(["사람", "날씨", "장소", "재료", "행동"]);
      } else if (food === "그것은 어떤음식과 매칭?") {
        addOptions(["떡볶이", "갈비탕", "돈가스", "죽"]);
      }

      let resolve = data.resolve;
      resolved.innerHTML = resolve;
    });
}

function addMessage(sender, message) {
  const msgDiv = document.createElement("div");
  msgDiv.classList.add(sender === "user" ? "myMsg" : "anotherMsg");
  msgDiv.innerHTML = '<span class="msg">' + message + "</span>";
  chatLog.appendChild(msgDiv);
}

window.addEventListener("load", function (e) {
  submit.addEventListener("click", send);
});
