package io.github.kenblizzard.fitly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

/**
 * Created by Ken on 3/4/2017.
 */

public class DataHandler {

    private static final String FILENAME = "fitly_routines.obj"; //file not found
    private static final String FILE_DIR = "data";
    public static ArrayList<Routine> listRoutines;

    private static File listRoutineFile;

    public static ArrayList<Routine> getRoutines() {
        return listRoutines;
    }

    public static Routine getRoutine(String label) {
        for (Routine rtn : listRoutines) {
            if (label == rtn.getLabel()) {
                return rtn;
            }
        }

        return null;
    }

    public static Routine addRoutine(Routine rtn) {
        listRoutines.add(rtn);
        return rtn;
    }

    public static String removeRoutine(int position) {
        String strRemoved = listRoutines.get(position).getLabel();
        listRoutines.remove(position);
        return strRemoved;
    }

    public static Routine editRoutine(Routine rtn, int position) {
        Routine toEdit = listRoutines.get(position);

        toEdit.createRoutine(rtn.getLabel(), rtn.getDescription(), rtn.duration, rtn.reps, rtn.rest);

        return toEdit;
    }

    public static Routine getRoutine(int index) {
        return listRoutines.get(index);
    }

    public static int getListRoutineSize() {
        return listRoutines.size();
    }

    public static void commit() {


        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(listRoutineFile, FILENAME)));
            out.writeObject(listRoutines);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Routine> getRoutineObject() {


        ObjectInputStream input;
        listRoutines = new ArrayList<>();

        try {
            input = new ObjectInputStream(new FileInputStream(new File(listRoutineFile, FILENAME)));
            listRoutines = (ArrayList<Routine>) input.readObject();
            input.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return listRoutines;

    }

    public static void createRoutines(File file) {

        if (listRoutineFile == null) {
            listRoutineFile = new File(file, FILE_DIR);
            listRoutineFile.mkdir();
        }


        listRoutines = getRoutineObject();

        if (listRoutines.size() == 0) {
            listRoutines = new ArrayList<>();
            Routine rtn = new Routine();
            rtn.createRoutine("Push-Ups", "push ups", 60, 3, 180);
            listRoutines.add(rtn);

            Routine rtn2 = new Routine();
            rtn2.createRoutine("Deadlift", "plank", 60, 3, 120);
            listRoutines.add(rtn2);


            Routine rtn3 = new Routine();
            rtn3.createRoutine("Curls-Up", "", 30, 4, 20);
            listRoutines.add(rtn3);
        }
    }


}
