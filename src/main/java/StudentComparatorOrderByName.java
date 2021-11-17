package main.java;

import java.util.Comparator;

public class StudentComparatorOrderByName implements Comparator<Student> {
	
	@Override
	public int compare(Student a, Student b)
    {
        return a.compareTo(b);
    }
}
