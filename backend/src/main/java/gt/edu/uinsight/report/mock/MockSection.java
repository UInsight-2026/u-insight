package	gt.edu.uinsight.report.mock;

public	class	MockSection	{
				private	final	Long	id;
				private	final	String	name;	//	"A",	"B",	"C"
				private	final	Long	courseId;
				private	final	String	courseCode;
				private	final	String	teacherCode;
				private	final	String	period;
				private	final	String	riskLevel;	//	LOW	|	MEDIUM	|	HIGH
				private	final	int	studentsAtRisk;
				public	MockSection(Long	id,	String	name,	Long	courseId,	String	courseCode,
																								String	teacherCode,	String	period,	String	riskLevel,	int	studentsAtRisk)	{
								this.id	=	id;
								this.name	=	name;
								this.courseId	=	courseId;
								this.courseCode	=	courseCode;
								this.teacherCode	=	teacherCode;
								this.period	=	period;
								this.riskLevel	=	riskLevel;
								this.studentsAtRisk	=	studentsAtRisk;
				}
				public	Long	getId()	{
								return	id;
				}
				public	String	getName()	{
								return	name;
				}
				public	Long	getCourseId()	{
								return	courseId;
				}
				public	String	getCourseCode()	{
								return	courseCode;
				}
				public	String	getTeacherCode()	{
								return	teacherCode;
				}
				public	String	getPeriod()	{
								return	period;
				}
				public	String	getRiskLevel()	{
								return	riskLevel;
				}
				public	int	getStudentsAtRisk()	{
								return	studentsAtRisk;
				}
}