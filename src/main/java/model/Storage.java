package model;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "STORAGES")
public class Storage {

    private long id;
    private String[] formatsSupported;
    private String storageCountry;
    private long storageSize;

    public Storage(String[] formatsSupported, String storageCountry, long storageSize) {
        this.formatsSupported = formatsSupported;
        this.storageCountry = storageCountry;
        this.storageSize = storageSize;
    }

    public Storage() {
    }

    @Id
    @SequenceGenerator(name = "STORAGE_SEQUENCE", sequenceName = "STORAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STORAGE_SEQUENCE")
    @Column(name = "ID")
    public long getId() {
        return id;
    }

    @Column(name = "FORMAT_SUPPORTED")
    @Convert(converter = FormatArrayConverter.class)
    public String[] getFormatsSupported() {
        return formatsSupported;
    }

    @Column(name = "STORAGE_COUNTRY")
    public String getStorageCountry() {
        return storageCountry;
    }

    @Column(name = "MAX_SIZE")
    public long getStorageSize() {
        return storageSize;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFormatsSupported(String[] formatsSupported) {
        this.formatsSupported = formatsSupported;
    }

    public void setStorageCountry(String storageCountry) {
        this.storageCountry = storageCountry;
    }

    public void setStorageSize(long storageSize) {
        this.storageSize = storageSize;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "id=" + id +
                ", formatsSupported=" + Arrays.toString(formatsSupported) +
                ", storageCountry='" + storageCountry + '\'' +
                ", storageSize=" + storageSize +
                '}';
    }
}
