package demo;

import controller.FileController;
import controller.StorageController;
import model.File;
import model.Storage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Demo {

    private static FileController fileController;
    private static StorageController storageController;


    public static void main(String[] args) throws Exception {

        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        fileController = context.getBean(FileController.class);
        storageController = context.getBean(StorageController.class);

        //crudStorage();
        //crudFile();
        //putDelete();
        //transferAll();
        transferFile();
    }

    private static void crudStorage() throws Exception {

        String[] formats = new String[]{"jpg", "txt"};
        Storage storage = new Storage(formats, "USA", 1200);

        System.out.println("saving storage...");
        System.out.println(storageController.save(storage));

        System.out.println("reading storage...");
        System.out.println(storageController.findById(storage.getId()));

        System.out.println("updating storage...");
        storage.setStorageSize(444L);
        System.out.println(storageController.update(storage));

        System.out.println("deleting storage...");
        storageController.delete(storage.getId());
        System.out.println(storageController.findById(storage.getId()));
    }

    private static void crudFile() throws Exception {

        String[] formats = new String[]{"jpg", "txt"};
        Storage storage1 = new Storage(formats, "USA", 1200);
        storageController.save(storage1);

        File file = new File("Fname", "jpg", 3, storage1);

        System.out.println("saving file...");
        System.out.println(fileController.save(file));

        System.out.println("reading file...");
        System.out.println(fileController.findById(file.getId()));

        System.out.println("updating file...");
        file.setSize(43L);
        System.out.println(fileController.update(file));

        System.out.println("deleting file...");
        fileController.delete(file.getId());
        //System.out.println(fileController.findById(file.getId()));
    }

    private static void putDelete() throws Exception {

        File file = fileController.findById(3);
        Storage storage = storageController.findById(7);
        Storage storage1 = storageController.findById(10);

        fileController.delete(storage1, file);
        System.out.println(fileController.put(storage, file));
        System.out.println(fileController.findById(file.getId()));
    }

    private static void transferAll() throws Exception {

        Storage from = storageController.findById(12);
        Storage to = storageController.findById(10);
        fileController.transferAll(from, to);
    }

    private static void transferFile() throws Exception {

        Storage from = storageController.findById(10);
        Storage to = storageController.findById(7);
        fileController.transferFile(from, to, 6);
    }
}
