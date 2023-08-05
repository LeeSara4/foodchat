const message = document.getElementById("chat");
const submit = document.getElementById("submit");
const chatLog = document.getElementById("chatLog");
const resolved = document.getElementById("resolved");
let optionList = [];

function addOptionList(obj) {
  optionList.push(obj);
}

function fetchData(obj) {
  return fetch("http://localhost:8080/foodchat/chat", {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=utf-8",
    },
    body: JSON.stringify(obj),
  }).then((resp) => resp.json());
}

function send(e) {
  if (e !== null) {
    e.preventDefault();
  }

  addMessage("myMsg", message.value);
  fetchData({ chat: message.value }).then(handleResponse);
}

function handleResponse(data) {
  message.value = "";
  // 추천의 답 -> chat : Y/N 날려주고, 지도로 가기
  if (data.answer !== undefined) {
    addMessage("anotherMsg", "이건 어떠세요?");
    addMessage("anotherMsg", data.answer);
    addOptions(["그래", "아닌듯"], "chat");
  }
  // 단어의 답 -> 단어의 정보들 날려주고, answer 받기
  if (data.request !== undefined) {
    addOptionList(obj = { request: data.request })
    addMessage("anotherMsg", "모르는 단어가 있다. '"+ data.request + "'가 뭐임");
    addOptions(["사람", "날씨", "장소", "재료", "행동"], "category");
  } 
}

function addOptions(options, id) {
  const optionsElement = document.createElement("div");
  optionsElement.id = "optionsDiv";
  options.forEach((option) => {
    const button = document.createElement("button");
    button.textContent = option;
    button.onclick = () => handleOptionSelect(option, id);
    optionsElement.appendChild(button);
  });
  chatLog.appendChild(optionsElement);
  scrollToBottom();
}

function handleOptionSelect(option, id) {
  const obj = {
    [id] : option,
  }
  addOptionList(obj)
  console.log(optionList);
  if (id === "chat") {
    optionList.pop();
  }
  if (option === "아닌듯") {
    addMessage("myMsg", option);
    fetchData(obj).then(handleResponse);
  } else if (id === "category" || option === "있어") {
    addMessage("myMsg", option);
    addMessage("anotherMsg", "그것은 어떤음식과 매칭?");
    addOptions(["떡볶이", "갈비탕", "돈가스", "죽"], "food");
  } else if (id === "food") {
    addMessage("myMsg", option);
    addMessage("anotherMsg", "또 있음?");
    addOptions(["있어", "없어"], "chat");
  } else if (option === "없어") {
    addMessage("myMsg", option);
    fetchData(optionList).then(handleResponse);
    optionList = [];
  }
  document.getElementById("optionsDiv").remove();
}

function addMessage(senderClass, message) {
  const msgDiv = document.createElement("div");
  msgDiv.classList.add(senderClass);
  msgDiv.innerHTML = '<span class="msg">' + message + "</span>";
  chatLog.appendChild(msgDiv);
  scrollToBottom();
}

function scrollToBottom() {
  var chatLog = document.getElementById("chatLog");
  chatLog.scrollTop = chatLog.scrollHeight;
}

window.addEventListener("load", () => {
  submit.addEventListener("click", send);
});