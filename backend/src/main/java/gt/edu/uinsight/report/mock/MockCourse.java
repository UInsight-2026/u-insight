package	gt.edu.uinsight.report.mock;

public	class	MockCourse	{
				private	final	Long	id;
				private	final	String	code;
				private	final	String	name;
				private	final	String	teacherCode;
				private	final	String	teacherName;
				private	final	String	period;
				private	final	int	totalStudents;
				public	MockCourse(Long	id,	String	code,	String	name,	String	teacherCode,
																							String	teacherName,	String	period,	int	totalStudents)	{
								this.id	=	id;
								this.code	=	code;
								this.name	=	name;
								this.teacherCode	=	teacherCode;
								this.teacherName	=	teacherName;
								this.period	=	period;
								this.totalStudents	=	totalStudents;
				}
				public	Long	getId()	{
								return	id;
				}
				public	String	getCode()	{
								return	code;
				}
				public	String	getName()	{
								return	name;
				}
				public	String	getTeacherCode()	{
								return	teacherCode;
				}
				public	String	getTeacherName()	{
								return	teacherName;
				}
				
				public	String	getPeriod()	{
								return	period;
				}
				public	int	getTotalStudents()	{
								return	totalStudents;
				}
			}