package main.java;

import java.util.Objects;

/**
 * The Class Student represents information of a student like name, GPA & RedId.
 */
public class Student implements Comparable<Student>{
	
	/** The name. */
	private String name;
	
	/** The gpa. */
	private Float gpa;
	
	/** The redId. */
	private Integer redId;
	
	/**
	 * Instantiates a new student.
	 */
	public Student() {
		super();
	}
	
	/**
	 * Instantiates a new student.
	 *
	 * @param name the name
	 * @param gpa the gpa
	 * @param redId the red id
	 */
	public Student(String name, Float gpa, Integer redId) {
		super();
		this.name = name;
		this.gpa = gpa;
		this.redId = redId;
	}

	/**
	 * Compares name of current student with another student.
	 *
	 * @param anotherStudent the another student
	 * @return the int
	 */
	@Override
	public int compareTo(Student anotherStudent) {
		return this.name.compareTo(anotherStudent.name);
	}

	/**
	 * Equals.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Student student = (Student) o;
		return this.name.equals(student.name);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Student [name=" + name + ", gpa=" + gpa + ", redId=" + redId + "]";
	}
	
	/**
	 * Compare GPA of current student with another student.
	 *
	 * @param anotherStudent the another student
	 * @return the int
	 */
	public int compareGPA(Student anotherStudent) {
		return this.gpa.compareTo(anotherStudent.gpa);
	}

}
