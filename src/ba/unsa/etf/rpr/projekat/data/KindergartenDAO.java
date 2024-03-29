package ba.unsa.etf.rpr.projekat.data;

import ba.unsa.etf.rpr.projekat.InvalidYearsOldException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class KindergartenDAO {
    private static KindergartenDAO instance;
    private Connection connection;
    private PreparedStatement giveAdminsStatement, giveParentsStatement, giveClassroomsStatement, giveChildrenStatement, giveTeachersStatement, giveAdminStatement, giveAdminByIdStatement, giveTeacherByIdStatement, giveClassroomByIdStatement, giveParentStatement, giveParentByIdStatement,giveChildByIdStatement, checkIfUsernameTakenAdminStatement, checkIfUsernameTakenParentStatement, insertNewParentStatement,
            insertNewChildStatement, insertNewClassroomStatement, insertNewTeacherStatement, parentIdMax, childIdMax, classroomMaxId, teacherMaxId, findFreeClassroomStatement, changeClassroomStatement;

    public static KindergartenDAO getInstance() {
        if(instance == null)
            instance = new KindergartenDAO();
        return instance;
    }

    private KindergartenDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:baza.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            giveAdminsStatement = connection.prepareStatement("SELECT admin.id, admin.name, admin.surname, admin.username, admin.password FROM admin");
        } catch (SQLException throwables) {
            regenerateDatabase();

            try {
                giveAdminsStatement = connection.prepareStatement("SELECT admin.id, admin.name, admin.surname, admin.username, admin.password FROM admin");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                giveAdminStatement = connection.prepareStatement("SELECT admin.id, admin.name, admin.surname, admin.username, admin.password FROM admin WHERE admin.username=? AND admin.password=?");
                giveAdminByIdStatement = connection.prepareStatement("SELECT admin.id, admin.name, admin.surname, admin.username, admin.password FROM admin WHERE admin.id=?");
                giveTeacherByIdStatement = connection.prepareStatement("SELECT teacher.id, teacher.name, teacher.surname, teacher.phoneNumber FROM teacher WHERE teacher.id=?");
                giveClassroomByIdStatement = connection.prepareStatement("SELECT classroom.id, classroom.children, classroom.teacher FROM classroom WHERE classroom.id=?");
                checkIfUsernameTakenAdminStatement = connection.prepareStatement("SELECT admin.id, admin.name, admin.surname, admin.username, admin.password FROM admin WHERE admin.username=?");
                checkIfUsernameTakenParentStatement = connection.prepareStatement("SELECT parent.id, parent.name, parent.surname, parent.username, parent.password, parent.status, parent.phoneNumber FROM parent WHERE parent.username=?");
                giveParentsStatement = connection.prepareStatement("SELECT parent.id, parent.name, parent.surname, parent.username, parent.password, parent.status, parent.phoneNumber FROM parent");
                giveClassroomsStatement = connection.prepareStatement("SELECT classroom.id, classroom.children, classroom.teacher FROM classroom");
                giveChildrenStatement = connection.prepareStatement("SELECT child.id, child.name, child.surname, child.parent1, child.yo, child.classroom FROM child");
                giveTeachersStatement = connection.prepareStatement("SELECT teacher.id, teacher.name, teacher.surname, teacher.phoneNumber FROM teacher");
                giveParentStatement = connection.prepareStatement("SELECT parent.id, parent.name, parent.surname, parent.username, parent.password, parent.status, parent.phoneNumber FROM parent WHERE parent.username=? AND parent.password=?");
                giveParentByIdStatement = connection.prepareStatement("SELECT parent.id, parent.name, parent.surname, parent.username, parent.password, parent.status, parent.phoneNumber FROM parent WHERE parent.id=?");
                giveChildByIdStatement = connection.prepareStatement("SELECT child.id, child.name, child.surname, child.parent1, child.yo, child.classroom FROM child WHERE child.id=?");
                insertNewParentStatement = connection.prepareStatement("INSERT INTO parent VALUES (?,?,?,?,?,?,?)");
                insertNewChildStatement = connection.prepareStatement("INSERT INTO child VALUES (?,?,?,?,?,?)");
                insertNewClassroomStatement = connection.prepareStatement("INSERT INTO classroom VALUES(?,?,?)");
                insertNewTeacherStatement = connection.prepareStatement("INSERT INTO teacher VALUES (?,?,?,?)");
                parentIdMax = connection.prepareStatement("SELECT MAX(id)+1 FROM parent");
                childIdMax = connection.prepareStatement("SELECT MAX(id)+1 FROM child");
                classroomMaxId = connection.prepareStatement("SELECT MAX(id)+1 FROM classroom");
                teacherMaxId = connection.prepareStatement("SELECT MAX(id)+1 FROM teacher");
                findFreeClassroomStatement = connection.prepareStatement("SELECT classroom.id, classroom.children, classroom.teacher FROM classroom WHERE (length(classroom.children) - length(replace(classroom.children, ',', '')))<"+String.valueOf(Classroom.getCapacity()));
                changeClassroomStatement = connection.prepareStatement("UPDATE classroom SET children=? WHERE id=?");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    public static void removeInstance() {
        if(instance == null) return;
        try {
            instance.connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        instance = null;
    }

    private void regenerateDatabase() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("baza.db.sql"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String sqlUpit = "";
        while(ulaz.hasNext()) {
            sqlUpit += ulaz.nextLine();
            if(sqlUpit.charAt(sqlUpit.length() - 1) == ';') {
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    statement.execute(sqlUpit);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                sqlUpit = "";
            }
        }
        ulaz.close();
    }

    public boolean loginCheckIfAdmin(String username, String password) {
        ResultSet resultSet = null;
        try {
            giveAdminStatement.setString(1, username);
            giveAdminStatement.setString(2, password);
            resultSet = giveAdminStatement.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e) {
            return false;
        }
        finally {
            try {
                if(resultSet != null)
                    resultSet.close();
                if(resultSet == null)
                    return false;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean loginCheckIfParent(String username, String password) {
        ResultSet resultSet = null;
        try {
            giveParentStatement.setString(1, username);
            giveParentStatement.setString(2, password);
            resultSet = giveParentStatement.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e) {
            return false;
        }
        finally {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean registerCheckIfUsernameTaken(String username) {
        ResultSet resultSetA = null;
        ResultSet resultSetP = null;
        try {
            checkIfUsernameTakenAdminStatement.setString(1, username);
            checkIfUsernameTakenParentStatement.setString(1, username);
            resultSetA = checkIfUsernameTakenAdminStatement.executeQuery();
            if(resultSetA.next())
                return true;
            resultSetP = checkIfUsernameTakenParentStatement.executeQuery();
            if(resultSetP.next())
                return true;
            return false;
        } catch (SQLException throwables) {
            return true;
        }
        finally {
            try {
                if(resultSetA != null)
                    resultSetA.close();
                if(resultSetP != null)
                    resultSetP.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public Parent addNewParentDB(String name, String surname, String username, String password, String maritalStatus, String phoneNumber) {
        Parent parent = null;
        int id = -1;
        try {
            ResultSet set = parentIdMax.executeQuery();
            if(set.next()) {
                insertNewParentStatement.setInt(1, set.getInt(1));
                id = set.getInt(1);
            }
            else {
                insertNewParentStatement.setInt(1, 1);
                id = 1;
            }
            insertNewParentStatement.setString(2, name);
            insertNewParentStatement.setString(3, surname);
            insertNewParentStatement.setString(4, username);
            insertNewParentStatement.setString(5, password);
            insertNewParentStatement.setString(6, maritalStatus);
            insertNewParentStatement.setInt(7, Integer.valueOf(phoneNumber));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            insertNewParentStatement.executeUpdate();
            parent = new Parent(id, name, surname, username, password, maritalStatus, Integer.valueOf(phoneNumber));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return parent;
    }

    public ArrayList<Parent> getAllParentsDB() {
        ResultSet resultSet = null;
        ArrayList<Parent> help = new ArrayList<>();
        try {
            resultSet = giveParentsStatement.executeQuery();
            while(resultSet.next()) {
                help.add(new Parent(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return help;
    }


    public void addNewChildDB(Parent parent, String name, String surname, int yo) {
        ResultSet resultSet, resultSet1;
        if(!(yo >= 1 && yo <= 6))
            return;
        int childId = -1;
        try {
            resultSet = childIdMax.executeQuery();
            if (resultSet.next()) {
                insertNewChildStatement.setInt(1, resultSet.getInt(1));
                childId = resultSet.getInt(1);
                System.out.println("child id: " + childId + " " + name + " " + surname);
            }
            else {
                insertNewChildStatement.setInt(1, 1);
                childId = 1;
            }
            insertNewChildStatement.setString(2, name);
            insertNewChildStatement.setString(3, surname);
            insertNewChildStatement.setInt(4, parent.getId());
            insertNewChildStatement.setInt(5, yo);

            //pronaći classroom
            resultSet1 = findFreeClassroomStatement.executeQuery();
            if(resultSet1.next()) {
                insertNewChildStatement.setInt(6, resultSet1.getInt(1));
                addChildToClassroom(resultSet1.getInt(1), childId, resultSet1.getString(2));

                insertNewChildStatement.executeUpdate();
            }
            else {
                int classroomId = addNewClassroomDB();
                insertNewChildStatement.setInt(6, classroomId);
                addChildToClassroom(classroomId, childId, "");

                insertNewChildStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int addNewClassroomDB() {
        int id = -1;
        try {
            ResultSet resultSet = classroomMaxId.executeQuery();
            if (resultSet.next()) {
                insertNewClassroomStatement.setInt(1, resultSet.getInt(1));
                id = resultSet.getInt(1);
            } else {
                insertNewClassroomStatement.setInt(1, 1);
                id = 1;
            }
            insertNewClassroomStatement.setString(2, "");
            insertNewClassroomStatement.setInt(3, 1); //TODO koja uciteljica se dodjeljuje
            insertNewClassroomStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void addChildToClassroom(int classroomId, int childId, String childrenList) {
        try {
            changeClassroomStatement.setString(1, childrenList + childId + ",");
            changeClassroomStatement.setInt(2, classroomId);
            changeClassroomStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Parent getParentByUsername(String username, String password) {
        Parent parent = null;
        try {

        giveParentStatement.setString(1, username);
        giveParentStatement.setString(2, password);
        ResultSet resultSet = giveParentStatement.executeQuery();
        if(resultSet.next())
            parent = new Parent(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7));
        }
        catch (SQLException s) {
            s.printStackTrace();
        }
        return parent;
    }

    public Admin getAdminByUsername(String username, String password) {
        Admin admin = null;
        try {

            giveAdminStatement.setString(1, username);
            giveAdminStatement.setString(2, password);
            ResultSet resultSet = giveAdminStatement.executeQuery();
            if(resultSet.next())
                admin = new Admin(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
        }
        catch (SQLException s) {
            s.printStackTrace();
        }
        return admin;
    }

    public ArrayList<Admin> getAllAdminsDB() {
        ResultSet resultSet = null;
        ArrayList<Admin> help = new ArrayList<>();
        try {
            resultSet = giveAdminsStatement.executeQuery();
            while(resultSet.next()) {
                help.add(new Admin(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return help;
    }

    public ArrayList<Classroom> getAllClassrooms() {
        ResultSet resultSet = null;
        ArrayList<Classroom> help = new ArrayList<>();
        try {
            resultSet = giveClassroomsStatement.executeQuery();
            while(resultSet.next()) {
                ArrayList<Child> childArrayList = getAllChildrenDBByIds(resultSet.getString(2));
                Teacher teacher = getTeacherById(resultSet.getInt(3));
                Classroom classroom = new Classroom(resultSet.getInt(1), childArrayList, teacher);
                help.add(classroom);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return help;
    }

    private Teacher getTeacherById(int id) {
        System.out.println(id);
        Teacher teacher = null;
        ResultSet resultSet;
        try {
            giveTeacherByIdStatement.setInt(1, id);
            resultSet = giveTeacherByIdStatement.executeQuery();
            if (resultSet.next()) {
                teacher = new Teacher(id, resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return teacher;
    }

    private ArrayList<Child> getAllChildrenDBByIds(String childrenIdsList) {
        ArrayList<Child> childArrayList = new ArrayList<>();
        if(childrenIdsList.equals(""))
            return childArrayList;
        String[] ids = childrenIdsList.split(",");
        ResultSet rs = null;
        for(int i = 0; i < ids.length; i++) {
            Child child = getChildByIdDB(Integer.parseInt(ids[i]));
            childArrayList.add(child);
        }
        return childArrayList;
    }

    private Child getChildByIdDB(int id) {
        Child child = null;
        System.out.println("child id: " + id);
        ResultSet resultSet;
        try {
            giveChildByIdStatement.setInt(1, id);
            resultSet = giveChildByIdStatement.executeQuery();
            if (resultSet.next()) {
                Parent parent1 = null;
                Parent parent2 = null;
                Classroom classroom = null;
                if(resultSet.getObject(4) != null)
                    parent1 = getParentById(resultSet.getInt(4));
                child = new Child(id, resultSet.getString(2), resultSet.getString(3), parent1, resultSet.getInt(6), null);
            }
        }
        catch (SQLException | InvalidYearsOldException s) {
            s.printStackTrace();
        }
        return child;
    }

    private Classroom getClassroomById(int id) {
        Classroom classroom = null;
        ResultSet resultSet;
        try {
            giveClassroomByIdStatement.setInt(1, id);
            resultSet = giveClassroomByIdStatement.executeQuery();
            classroom = new Classroom(id, getAllChildrenDBByIds(resultSet.getString(2)), getTeacherById(resultSet.getInt(3)));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return classroom;
    }

    private Parent getParentById(int id) {
        Parent parent = null;
        ResultSet resultSet;
        try {
            giveParentByIdStatement.setInt(1, id);
            resultSet = giveParentByIdStatement.executeQuery();
            parent = new Parent(id, resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return parent;
    }

    public ArrayList<Child> getChildrenDB() {
        ArrayList<Child> children = new ArrayList<>();
        ResultSet rs;
        try {
            rs = giveChildrenStatement.executeQuery();
            while(rs.next()) {
                Child child = new Child(rs.getInt(1), rs.getString(2), rs.getString(3), getParentById(rs.getInt(4)), rs.getInt(5), getClassroomById(rs.getInt(6)));
                children.add(child);
            }
        }
        catch (SQLException s) {
            s.printStackTrace();
        }
        catch (InvalidYearsOldException e) {
            e.printStackTrace();
        }
        return children;
    }

    public ArrayList<Teacher> getAllTeachersDB() {
        ArrayList<Teacher> teachers = new ArrayList<>();
        ResultSet resultSet;
        try {
            resultSet = giveTeachersStatement.executeQuery();
            while(resultSet.next()) {
                Teacher teacher = new Teacher(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4));
                teachers.add(teacher);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    public Connection getConnection() {
        return connection;
    }

    public int addNewClassroomDB(Teacher t) {
        int id = -1;
        try {
            ResultSet resultSet = classroomMaxId.executeQuery();
            if (resultSet.next()) {
                insertNewClassroomStatement.setInt(1, resultSet.getInt(1));
                id = resultSet.getInt(1);
            } else {
                insertNewClassroomStatement.setInt(1, 1);
                id = 1;
            }
            insertNewClassroomStatement.setString(2, "");
            insertNewClassroomStatement.setInt(3, t.getId());
            insertNewClassroomStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public int addNewTeacherDB(String name, String surname, String phoneNumber) {
        int id = -1;
        try {
            ResultSet resultSet = teacherMaxId.executeQuery();
            if(resultSet.next()) {
                insertNewTeacherStatement.setInt(1, resultSet.getInt(1));
                id = resultSet.getInt(1);
            }
            else {
                insertNewTeacherStatement.setInt(1, 1);
                id = 1;
            }
            insertNewTeacherStatement.setString(2, name);
            insertNewTeacherStatement.setString(3, surname);
            insertNewTeacherStatement.setInt(4, Integer.parseInt(phoneNumber));
            insertNewTeacherStatement.executeUpdate();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}