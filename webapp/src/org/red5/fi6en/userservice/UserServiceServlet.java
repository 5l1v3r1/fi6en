package org.red5.fi6en.userservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.red5.fi6en.core.Application;
import org.red5.fi6en.util.HibernateUtil;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.Red5;
import org.slf4j.Logger;

public class UserServiceServlet extends HttpServlet {
	
	static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	private static Logger log = Red5LoggerFactory.getLogger(Application.class,"fi6en");
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html;charset=UTF-8");
		String roomname = request.getParameter("roomname");
		if (roomname == null) roomname = "";
		
		StringBuilder sb = new StringBuilder();
		
		//Select * from files
		Session session = sessionFactory.openSession();
		String sql = "select * from user_status where is_online = 1 AND roomname = '" + roomname + "'";
		System.out.println(sql);
		
		SQLQuery query = session.createSQLQuery(sql);
        query.addEntity("user_status", UserStatus.class);
        List results = query.list();
        Iterator iter = results.iterator();
		
		sb.append("<users>");
		while (iter.hasNext()) {
			UserStatus u = (UserStatus) iter.next();
			
			sb.append("<user>");
			
			sb.append("<id>" + u.getId() + "</id>");
			sb.append("<username>" + u.getUsername() + "</username>");
			sb.append("<roomname>" + u.getRoomname() + "</roomname>");
			
			sb.append("</user>");
		}
		sb.append("</users>");

		PrintWriter out = response.getWriter();
		out.print(sb.toString());

	}

}