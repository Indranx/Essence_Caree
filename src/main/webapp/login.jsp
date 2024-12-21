<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Test Login</title>
    <style>
      .error {
        color: red;
      }
      .success {
        color: green;
      }
      pre {
        background: #f4f4f4;
        padding: 10px;
      }
    </style>
  </head>
  <body>
    <h2>Test Login Form</h2>
    <form id="loginForm">
      <div>
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" value="kminchelle" />
      </div>
      <div>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" value="0lelplR" />
      </div>
      <button type="submit">Login</button>
    </form>
    <div id="result"></div>
    <pre id="requestInfo"></pre>

    <script>
      document
        .getElementById("loginForm")
        .addEventListener("submit", async (e) => {
          e.preventDefault();
          const username = document.getElementById("username").value;
          const password = document.getElementById("password").value;

          const requestBody = JSON.stringify({ username, password });
          document.getElementById(
            "requestInfo"
          ).textContent = `Request Body: ${requestBody}\n`;

          try {
            const response = await fetch("/api/auth/login", {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
              },
              body: requestBody,
            });

            const data = await response.json();
            document.getElementById("requestInfo").textContent +=
              `Response Status: ${response.status}\n` +
              `Response Headers: ${JSON.stringify(
                Object.fromEntries(response.headers),
                null,
                2
              )}\n` +
              `Response Body: ${JSON.stringify(data, null, 2)}`;

            if (response.ok) {
              document.getElementById("result").className = "success";
              document.getElementById("result").textContent =
                "Login successful!";
            } else {
              document.getElementById("result").className = "error";
              document.getElementById("result").textContent =
                data.message || "Login failed";
            }
          } catch (error) {
            document.getElementById("result").className = "error";
            document.getElementById("result").textContent =
              "Error: " + error.message;
            document.getElementById(
              "requestInfo"
            ).textContent += `\nError Details: ${error.stack}`;
          }
        });
    </script>
  </body>
</html>
