const url = "http://localhost:8080/foodchat/";
const message = document.getElementById("chat");
const submit = document.getElementById("submit");
const chatLog = document.getElementById("chatLog");
const resolved = document.getElementById("resolved");
const listBox = document.getElementById("listBox");

let optionList = [];
let foodList = [];
let nope = 0;

function firstMsg() {
  addMessage("anotherMsg", "안녕하세요. 음식 추천을 도와드릴 Dungs 입니다!");
  addMessage("anotherMsg", "아무 말이나 적어보세요!");
}

function fetchData(obj, method, seturl = url + "chat") {
  return fetch(seturl, {
    method: method,
    headers: {
      "Content-Type": "application/json;charset=utf-8",
    },
    body: JSON.stringify(obj),
  });
}

function setFoodList() {
  fetch(url)
    .then((resp) => resp.json())
    .then((data) => {
      foodList.push(...data.list);
    });
}

function addOptionList(key, value) {
  let obj = {
    [key]: value,
  };
  optionList.push(obj);
  console.log(optionList);
}

function send(e) {
  if (e !== null) {
    e.preventDefault();
  }

  if (message.value.trim() === "") {
    return;
  }

  addMessage("myMsg", message.value);
  fetchData({ chat: message.value }, "POST")
    .then((resp) => resp.json())
    .then(handleResponse);
  message.value = "";
}

function handleResponse(data) {
  // 추천의 답 -> chat : Y/N 날려줌
  if (data.answer !== undefined) {
    if (data.answer === "") {
      addMessage(
        "anotherMsg",
        "이해가 잘 안되네요. 어려운 말이 있나 봐요. 다시 말해줄 수 있을까요?"
      );
    } else {
      chatBotAnswer();
      addOptionList("request", data.answer);
      localStorage.setItem("keyword", data.answer);
      addMessage("anotherMsg", data.answer + " 어때요?");
      addMsg(
        "anotherMsg",
        "<img src = '" +
          data.img +
          "' referrerpolicy='no-referrer' height = '300' />"
      );
      addOptions(["좋아!", "별로야"], "request");
    }
  }
  // 모르는 단어의 답 -> 단어의 정보들 날려줌
  if (data.request !== undefined) {
    addOptionList("request", data.request);
    addMessage(
      "anotherMsg",
      "모르는 단어가 포함되어 있었어요.. '" +
        data.request +
        "' 이(가) 음식인가요?"
    );
    addOptions(["응", "아니"], "isFood");
  }
  // 사용자가 음식만 입력했을 때
  if (data.ask !== undefined) {
    addOptionList("ask", data.ask);
    localStorage.setItem("keyword", data.ask);
    addMessage(
      "anotherMsg",
      "'" + data.ask + "' 과 어울리는 음식을 소개해 드릴까요?"
    );
    addOptions(["소개해줘", data.ask + " 맛집 보러가기"], "ask");
  }
}

function addOptions(options, id) {
  setTimeout(() => {
    message.disabled = true;
    submit.disabled = true;
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
  }, 1500);
}

function handleOptionSelect(option, id) {
  message.disabled = false;
  submit.disabled = false;

  addMessage("myMsg", option);
  if (id === "request") {
    if (option === "별로야") {
      nope = nope + 1;
      addOptionList("category", "거절");
      addMessage("anotherMsg", "그럼 어떤게 먹고 싶어요?");
    } else if (option === "좋아!") {
      addOptionList("category", "수락");
      addMessage("anotherMsg", "근처 음식점을 소개해줄게요!");
      addMsg(
        "anotherMsg",
        "<a href = 'view/location.html'>음식점 보러가기</a>"
      );
    }
    fetchData(optionList, "PUT");
    optionList = [];
  } else if (id === "ask") {
    if (option === "소개해줘") {
      fetchData(optionList, "POST", url + "ask")
        .then((resp) => resp.json())
        .then(handleResponse);
    } else {
      addMessage("anotherMsg", "근처 음식점을 소개해줄게요!");
      addMsg(
        "anotherMsg",
        "<a href = 'view/location.html'>음식점 보러가기</a>"
      );
    }
    optionList = [];
  } else if (id === "isFood") {
    if (option === "응") {
      addOptionList("category", "음식");
      fetchData(optionList, "PUT");
      optionList = [];
      addMessage("anotherMsg", "이제 물어봐 주세요.");
      toast("감사합니다! 저장했어요.", "success");
    } else if (option === "아니") {
      addMessage("anotherMsg", "그건 음식을 연상할 수 있는 단어인가요?");
      addOptions(["응", "아니"], "isWord");
    }
  } else if (id === "isWord") {
    if (option === "응") {
      addOptionList("category", "단어");
    } else if (option === "아니") {
      addOptionList("category", "예외");
    }
    fetchData(optionList, "PUT");
    optionList = [];
    addMessage("anotherMsg", "이제 물어봐 주세요.");
    toast("감사합니다! 저장했어요.", "success");
  }
  document.getElementById("optionsDiv").remove();
}

function userAnswer() {
  let yes = ["그래", "응", "이해", "알", "오케", "맞아", "좋"];
  let no = ["아니", "거절", "별로", "싫어", "안"];
  return;
}

function chatBotAnswer() {
  let first = [
    "제가 추천하는 음식은 이거에요!",
    "이걸 드셔보는 건 어떠세요?",
    "이건 어떠세요?",
  ];
  let second = [
    "흠.. 그러면 이건 어떠세요?",
    "드디어 이해 했어요! 이걸 드시는건 어때요?",
    "그냥 이거 드셔주실래요?",
  ];
  let third = [
    "너 되게 까다롭구나?",
    "제발 그냥 먹어라",
    "그냥 이거 무조건 먹어라.",
  ];
  var num = Math.floor(Math.random() * 3);

  if (nope === 0) {
    addMessage("anotherMsg", first[num]);
    toast("선호하는 맛을 이야기 해보세요!", "info");
  } else if (nope === 1) {
    addMessage("anotherMsg", second[num]);
    toast("더 자세히 이야기 해보세요!", "info");
  } else if (nope === 2) {
    addMessage("anotherMsg", third[0]);
    toast("이제 고를때가 된 것 같은데요!", "info");
  } else if (nope === 3) {
    addMessage("anotherMsg", third[1]);
  } else if (nope === 4) {
    addMessage("anotherMsg", third[2]);
  } else {
	nope = 0;
    addMessage("anotherMsg", "아 쫌!!!");
  }
}

function addMsg(senderClass, message) {
  setTimeout(() => {
    const msgDiv = document.createElement("div");
    msgDiv.classList.add(senderClass);
    msgDiv.innerHTML = '<span class="msg">' + message + "</span>";
    chatLog.appendChild(msgDiv);
    scrollToBottom();
  }, 1500);
}

function addMessage(senderClass, message) {
  const nameDiv = document.createElement("div");
  const msgDiv = document.createElement("div");
  msgDiv.classList.add(senderClass);
  if (senderClass === "anotherMsg") {
    nameDiv.innerHTML = "<span class='anotherName'>DUNGS</span>";
  } else if (senderClass === "myMsg") {
    nameDiv.innerHTML = "<span class='myName'>ME</span>";
  }
  msgDiv.innerHTML = '<span class="msg"></span>';
  chatLog.appendChild(nameDiv);
  chatLog.appendChild(msgDiv);
  simulateTyping(message, msgDiv.querySelector(".msg"));
  scrollToBottom();
}

function simulateTyping(content, element) {
  let i = 0;
  const typingInterval = 60;
  const typingDelay = 500;

  setTimeout(function () {
    const typing = setInterval(function () {
      if (element && i < content.length) {
        element.textContent += content[i++];
      } else {
        clearInterval(typing);
      }
    }, typingInterval);
  }, typingDelay);
  scrollToBottom();
}

function scrollToBottom() {
  var chatLog = document.getElementById("chatLog");
  chatLog.scrollTop = chatLog.scrollHeight;
}

function handleButtonClick(e) {
  send(e);
}

window.addEventListener("load", () => {
  firstMsg();
  document.getElementById("map").addEventListener("click", () => {
    window.location.href = "view/map.html";
  });
  submit.addEventListener("click", handleButtonClick);
});

function toast(text, icon) {
  const Toast = Swal.mixin({
    toast: true,
    position: "top",
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    didOpen: (toast) => {
      toast.addEventListener("mouseenter", Swal.stopTimer);
      toast.addEventListener("mouseleave", Swal.resumeTimer);
    },
  });

  setTimeout(function () {
    Toast.fire({
      icon: icon,
      title: text,
    });
  }, 2000);
}
