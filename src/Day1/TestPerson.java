package Day1;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;



public class TestPerson {
	
	SessionFactory factory;
	
	/* Setting up Hibernate & create session factory*/
	public void setup(){
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
		srBuilder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
		factory = configuration.buildSessionFactory(serviceRegistry);
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TestPerson tp = new TestPerson();
		tp.setup();
		
		Session session = tp.factory.openSession();
		Transaction tx = session.beginTransaction();
		Employee e1 = new Employee("Awantik");
		
		session.save(e1);
		tx.commit();
		session.close();
		
	}

}
