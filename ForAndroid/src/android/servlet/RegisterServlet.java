package android.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.bean.Person;
import android.service.PersonService;



public class RegisterServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String phone = request.getParameter("phone");
		
		Person person  = new Person();
		person.setName(name);
		person.setPassword(password);
		person.setPhone(phone);
		
		PersonService personService = new PersonService();
		int num = personService.registerPerson(person);
		
		response.getWriter().write(num + "");
		
	}

}
