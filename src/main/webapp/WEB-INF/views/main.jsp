<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>


    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css">
    <script src="${pageContext.request.contextPath}/resources/openSend.js"></script>

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
