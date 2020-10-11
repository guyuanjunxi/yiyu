package android.service;

import android.bean.Person;
import android.dao.PersonDAO;


public class PersonService {
	

	PersonDAO personDAO = new PersonDAO();
	
	public int loginPerson(Person person){
		return personDAO.loginPerson(person);
	}
	
	public int registerPerson(Person person){
		return personDAO.registerPerson(person);
	}
	
	public int forgetPassword(Person person){
		return personDAO.forgetPassword(person);
	}
	
	public int changePassword(Person person){
		return personDAO.changePassword(person);
	}
	
	public int deletePerson(Person person){
		return personDAO.deletePerson(person);
	}

}
