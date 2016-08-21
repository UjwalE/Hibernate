package Day2;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class TestAssignment {

	SessionFactory factory;
	
	public void setup(){
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
		srBuilder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
		factory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	
	public Skill findSkill(Session session, String name){
		Query query = session.createQuery("from Skill s where s.name=:name");
		query.setParameter("name", name);
		Skill skill = (Skill) query.uniqueResult();
		return skill;
	}
	
	public Skill saveSkill(Session session, String name){
		Skill skill = findSkill(session,name); 
		if (skill == null){
			skill = new Skill();
			skill.setName(name);
			session.save(skill);
		}
		return skill;
	}
	
	public Student findStudent(Session session, String name){
		Query query = session.createQuery("from Student s where s.name=:name");
		query.setParameter("name", name);
		Student student = (Student) query.uniqueResult();
		return student;
	}
	
	public Student saveStudent(Session session, String name){
		Student student = findStudent(session,name); 
		if (student == null){
			student = new Student();
			student.setName(name);
			session.save(student);
		}
		return student;
	}
	
	public void createData(Session session, String subjectName, String observerName, String skillName, int rank){
		Student subject = saveStudent(session,subjectName);
		Student observer = saveStudent(session,observerName);
		Skill skill = saveSkill(session,skillName);
		
		Ranking ranking = new Ranking();
		ranking.setSubject(subject);
		ranking.setObserver(observer);
		ranking.setSkill(skill);
		ranking.setRating(rank);
		
		session.save(ranking);
		
	}
	
	public void changeRank(Session session, String subjectName, String observerName, String skillName, int newRating){
		/*Query query = session.createQuery("from Ranking r "
				+ "where r.subject.name=:subject and "
				+ "r.observer.name=:observer and "
				+ "r.skill.name=:skill");
		query.setString("subject", subjectName);
		query.setString("observer",observerName);
		query.setString("skill", skillName);
		*/
		
		// Reusing the code for getting rank by getRank() method
		Ranking ranking = (Ranking) getRank(session, subjectName, observerName, skillName);
		
		/*if(ranking == null){
			System.out.println("Invalid Change Request");
		}
		*/
		ranking.setRating(newRating);
		
	    
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TestAssignment tp = new TestAssignment();
		tp.setup();
		
		Session session = tp.factory.openSession();
		Transaction tx = session.beginTransaction();
		
		// Add ranks for default data.
	tp.createData(session, "Joe","Zabaleta","Java",5);
	tp.createData(session, "Nolito","Aguero","Hibernate",7);
	tp.createData(session, "Aguero","Kolarov","Spring",10);
	tp.createData(session, "Silva","Joe","Spring",9);
	tp.createData(session, "Joe","Aguero","Hibernate",6);
	tp.createData(session, "Joe","Hart","Spring",7);
	tp.createData(session, "Sterling","Joe","Spring",6);
	tp.createData(session, "Otamendi","Fernandinho","Hibernate",7);
	tp.createData(session, "Fernandinho","Silva","Java",6);
	tp.createData(session, "Aguero","Joe","Hibernate",9);
	tp.createData(session, "Bony","Aguero","Java",2);
	tp.createData(session, "Silva","Fernandinho","Hibernate",10);
	
		
	
	/*As for assignment all operation are coded below in sequence and commented out,
	 * Uncomment the operations to be performed. */

	//Displaying all the available Ranking in data base.
	displayRankings(session);
		
	//Task 1:Removing a rank.
	//tp.deleteRank(session,"Joe","Hart","Spring");
	
	//Task 2:Get Average of students for each skill
	/*double avg = tp.getAvgOfSkill(session,"Spring");
	System.out.println("Average Rating of students for Spring is "+ avg);
	*/
	
	//Task 3:Sort by rating of Students for a skill
	/*List<Ranking> sortedList = sortByRank(session,"Spring");
	if(sortedList != null){
		showList(sortedList);
	}else{
		System.out.println("Error");
	}*/
	
	//Task 4: Finding Topper
	//to get topper for a particular skill enter the skill in below line
	String skill = "Hibernate";
	//to get overall topper set above skill variable null
	
	int maxRating =  getMaxRating(session,skill);
	findToppers(session,skill, maxRating);

	
	tx.commit();
	session.close();
	
}

	private static void findToppers(Session session, String skill, int rating) {
		// TODO Auto-generated method stub
		Query query = null;
		String msg =null;
		if(skill ==null || skill.equals("")){
			msg = "Overall Topper";
			query = session.createQuery("from Ranking r "
					+"where r.rating=:rating");
		}else{
			msg= "Topper for "+skill;
		query = session.createQuery("from Ranking r "
				+"where r.skill.name=:skill "
				+ "and r.rating=:rating");
		query.setString("skill", skill);		
		}
		query.setInteger("rating", rating);
		
		List<Ranking> list = query.list();
		
		System.out.println(msg+":");
		for (Ranking rank : list){
			System.out.println(rank.getSubject().getName());
		}
		
	}


	private static int getMaxRating(Session session, String skill) {
		// TODO Auto-generated method stub
		Query query = null;
		if(skill ==null || skill.equals("")){
			
			query = session.createQuery("select max(rating) from Ranking r ");
		}else{
		query = session.createQuery("select max(rating) from Ranking r "
				+"where r.skill.name=:skill");
		query.setString("skill", skill);
		}
		return (Integer)query.uniqueResult();			
	}


	private static void showList(List<Ranking> list) {
		// TODO Auto-generated method stub
		System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out.printf("%-22s%-22s%-22s%-22s\n","Subject","Observer","Skill","Rating");
		
		for (Ranking rank : list) {
			System.out.printf("%-22s%-22s%-22s%-22d\n",
					rank.getSubject().getName(),rank.getObserver().getName(),
					rank.getSkill().getName(),rank.getRating());
		}
		
	}


	private static List<Ranking> sortByRank(Session session, String skill) {
		// TODO Auto-generated method stub
		List<Ranking> list = null;
		
		Query query = session.createQuery("from Ranking r "
				+"where r.skill.name=:skill "
				+"order by r.rating desc");
		query.setString("skill", skill);
		
		return query.list();
		
	}


	private double getAvgOfSkill(Session session, String skill) {
		// TODO Auto-generated method stub
		Query query = session.createQuery("select avg(rating) from Ranking r "
				+"where r.skill.name=:skill");
		query.setString("skill", skill);
		return (Double)query.uniqueResult();
		
	}


	private static void displayRankings(Session session) {
		// TODO Auto-generated method stub
		
		List<Ranking> rankings = getAllRanking(session);
		showList(rankings);
	}

	private static List getAllRanking(Session session) {
		// TODO Auto-generated method stub
		List<Ranking> rankings = session.createQuery("from Ranking").list();
		return rankings;
	}
	
	//Function to get rank for a particular inputs.
		public Ranking getRank(Session session, String subjectName, String observerName, String skillName){
			Query query = session.createQuery("from Ranking r "
					+ "where r.subject.name=:subject and "
					+ "r.observer.name=:observer and "
					+ "r.skill.name=:skill");
			query.setString("subject", subjectName);
			query.setString("observer",observerName);
			query.setString("skill", skillName);
			
			Ranking ranking = null;
			ranking = (Ranking) query.uniqueResult();
			
			if(ranking == null){
				System.out.println("Invalid Change Request");
			}
			
			return ranking;
		}
		
		// Function for delete the rank for particular input
		public void deleteRank(Session session, String subjectName, String observerName, String skillName){
			
			Ranking ranking = (Ranking) getRank(session, subjectName, observerName, skillName);
			session.delete(ranking);
			
		}

}
