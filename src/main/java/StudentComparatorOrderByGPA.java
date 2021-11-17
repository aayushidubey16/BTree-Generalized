package main.java;

import java.util.Comparator;

public class StudentComparatorOrderByGPA implements Comparator<Student> {

	@Override
	public int compare(Student a, Student b)
    {
        return a.compareGPA(b);
    }
	
}
