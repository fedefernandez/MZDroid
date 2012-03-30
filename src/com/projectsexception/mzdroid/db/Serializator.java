package com.projectsexception.mzdroid.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.projectsexception.mzdroid.util.CustomLog;

import android.content.Context;

public class Serializator {

    private static final Object lock = new Object();

    public static enum Type {
        LEAGUE_TEAM("leagueTeam.ser"), NEXT_MATCHES("nextMatches.ser"), PLAYED_MATCHES(
                "playedMatches.ser");

        private String file;

        private Type(String file) {
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }

    public static void saveObject(Context context, Serializable object,
            Serializator.Type type) {
        if (context != null) {
            synchronized (lock) {
                try {
                    FileOutputStream cacheFile = context.openFileOutput(
                            type.getFile(), Context.MODE_PRIVATE);
                    ObjectOutputStream out = new ObjectOutputStream(cacheFile);
                    out.writeObject(object);
                    out.close();
                    cacheFile.close();
                } catch (FileNotFoundException e) {
                    CustomLog.error("Serializator", e.getMessage());
                } catch (IOException e) {
                    CustomLog.error("Serializator", e.getMessage());
                }
            }
        }
    }

    public static Object loadObject(Context context, Serializator.Type type) {
        Object object = null;
        if (context != null) {
            synchronized (lock) {
                try {
                    FileInputStream fileIn = context.openFileInput(type
                            .getFile());
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    object = in.readObject();
                    in.close();
                    return object;
                } catch (FileNotFoundException e) {
                    CustomLog.info("Serializator", "No cached instance of "
                            + type);
                    return object;
                } catch (IOException e) {
                    CustomLog.error("Serializator", e.getMessage());
                    return object;
                } catch (ClassNotFoundException e) {
                    CustomLog.error("Serializator", e.getMessage());
                    return object;
                }
            }
        } else {
            return object;
        }
    }

}
