package main.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.BTreeSet;
import main.java.Student;
import main.java.StudentComparatorOrderByGPA;
import main.java.StudentComparatorOrderByName;

/**
 * The Class BTreeSetTest.
 */
class BTreeSetTest {
	
	/** The b tree set. */
	BTreeSet<Student> bTreeSet;
     
	/**
	 * Sets the up the bTreeSet for testing method testAddAndContains, 
	 * testInternalIterator & testToArray.
	 */
	@BeforeEach
    void setUp() {
    	bTreeSet = new BTreeSet<Student>();
    	bTreeSet.add(new Student("A",4.0f,825874450));
		bTreeSet.add(new Student("B",1.0f,825874451));
		bTreeSet.add(new Student("C",2.0f,825874452));
		bTreeSet.add(new Student("D",4.0f,825874453));
		bTreeSet.add(new Student("E",4.0f,825874454));
		bTreeSet.add(new Student("F",2.5f,825874455));
    }

	/**
	 * Test add and contains method of BTreeSet.
	 */
	@Test
	void testAddAndContains() {
		Student student = new Student("Zz",4.0f,825874466);
		bTreeSet.add(student);
		assertEquals(true, bTreeSet.contains(student));
	}

	/**
	 * Test internal iterator through forEach method.
	 */
	@Test
	void testInternalIterator() {
		List<Student> students = new ArrayList<Student>();
		bTreeSet.forEach(students :: add);
		List<Student> testStudents = Arrays.asList(new Student("F", 2.5f, 825874455), new Student("E", 4.0f, 825874454),
				new Student("D", 4.0f, 825874453), new Student("C", 2.0f, 825874452), new Student("B", 1.0f, 825874451),
				new Student("A", 4.0f, 825874450));
		assertIterableEquals(testStudents, students);
	}

	/**
	 * Test to array.
	 */
	@Test
	void testToArray() {
		Object[] students = bTreeSet.toArray();
		Object[] testStudents = {new Student("A", 4.0f, 825874450), new Student("B", 1.0f, 825874451),
				new Student("C", 2.0f, 825874452), new Student("D", 4.0f, 825874453), new Student("E", 4.0f, 825874454),
				new Student("F", 2.5f, 825874455)};
		assertArrayEquals(testStudents, students);
	}

	/**
	 * Test iterator.
	 */
	@Test
	void testIterator() {
		Iterator<Student> iterator = bTreeSet.iterator();
		Object[] testStudents = {new Student("A", 4.0f, 825874450), new Student("B", 1.0f, 825874451),
				new Student("C", 2.0f, 825874452), new Student("D", 4.0f, 825874453), new Student("E", 4.0f, 825874454),
				new Student("F", 2.5f, 825874455)};
		List<Student> students = new ArrayList<Student>();
		while(iterator.hasNext()) {
			Student student = iterator.next();
			students.add(iterator.next());
		}
		assertIterableEquals(Arrays.asList(testStudents), students);
	}
	
	/**
	 * Test stretegy patter order by GPA.
	 */
	@Test
	void testStretegyPatterOrderByGPA() {
		BTreeSet<Student> bTreeSet = new BTreeSet<>(new StudentComparatorOrderByGPA());
    	bTreeSet.add(new Student("A",3.0f,825874450));
		bTreeSet.add(new Student("B",1.0f,825874451));
		bTreeSet.add(new Student("C",2.0f,825874452));
		bTreeSet.add(new Student("D",3.5f,825874453));
		bTreeSet.add(new Student("E",3.3f,825874454));
		bTreeSet.add(new Student("F",2.5f,825874455));
		Object[] students = bTreeSet.toArray();
		Object[] testStudents = {new Student("B",1.0f,825874451), new Student("C",2.0f,825874452),
				new Student("F",2.5f,825874455), new Student("A",3.0f,825874450), new Student("E",3.3f,825874454),
				new Student("D",3.5f,825874453)};
		assertArrayEquals(testStudents, students);
	}
	
	/**
	 * Test stretegy patter order by name.
	 */
	@Test
	void testStretegyPatterOrderByName() {
		BTreeSet<Student> bTreeSet = new BTreeSet<>(new StudentComparatorOrderByName());
    	bTreeSet.add(new Student("A",3.0f,825874450));
		bTreeSet.add(new Student("B",1.0f,825874451));
		bTreeSet.add(new Student("C",2.0f,825874452));
		bTreeSet.add(new Student("D",3.5f,825874453));
		bTreeSet.add(new Student("E",3.3f,825874454));
		bTreeSet.add(new Student("F",2.5f,825874455));
		Object[] students = bTreeSet.toArray();
		Object[] testStudents = {new Student("A", 4.0f, 825874450), new Student("B", 1.0f, 825874451),
				new Student("C", 2.0f, 825874452), new Student("D", 4.0f, 825874453), new Student("E", 4.0f, 825874454),
				new Student("F", 2.5f, 825874455)};
		assertArrayEquals(testStudents, students);
	}
}
