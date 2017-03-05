package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import db.DataBase;
import model.HttpMethod;
import model.HttpRequest;
import model.RequestLine;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	@Override
	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream();
			 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {

			HttpRequest httpRequest = new HttpRequest();

			// RequestList 읽기
			String line = bufferedReader.readLine();
			if (line == null) {
				return;
			}
			httpRequest.setRequestLine(line);

			// Header 읽기
			while(!(line = bufferedReader.readLine()).equals("")) {
				log.debug(line);

				String[] headerItem = line.split(":");
				httpRequest.addHeaderItem(headerItem[0], headerItem[1].trim());
			}

			// HttpMethod가 POST라면 Body 읽기
			if (HttpMethod.POST.is(httpRequest)) {
				int contentLength = Integer.parseInt(httpRequest.getHeaderItem("Content-Length"));
				httpRequest.setBodyParamMap(IOUtils.readData(bufferedReader, contentLength));
			}

			String path = httpRequest.getRequestLine().getPath();
			if ("/user/create".startsWith(path)) {
				Map<String, String> paramMap = httpRequest.getParamMap();

				User user = new User(paramMap.get("userId"), paramMap.get("password"), paramMap.get("name"), paramMap.get("email"));
				DataBase.addUser(user);

				response302Header(out, "/index.html");
				return;

			}

			if ("/user/login".startsWith(path)) {
				Map<String, String> paramMap = httpRequest.getParamMap();

				User user = DataBase.findUserById(paramMap.get("userId"));
				if (user == null) {
					responseLoginFail(out);
					return;
				}

				if (user.getPassword().equals(paramMap.get("password"))) {
					responseLoginSuccess(out);
					return;
				}

				responseLoginFail(out);
			}

			DataOutputStream dos = new DataOutputStream(out);
			byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseLoginSuccess(OutputStream out) {
		DataOutputStream dos = new DataOutputStream(out);

		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: /index.html\r\n");
			dos.writeBytes("Set-Cookie: logined=true\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseLoginFail(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		byte[] body = Files.readAllBytes(new File("./webapp/user/login_failed.html").toPath());
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
		dos.writeBytes("Content-Length: " + body.length + "\r\n");
		dos.writeBytes("Set-Cookie: logined=false");
		dos.writeBytes("\r\n");

		responseBody(dos, body);
	}

	private void response302Header(OutputStream out, String path) {
		DataOutputStream dos = new DataOutputStream(out);

		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + path + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
