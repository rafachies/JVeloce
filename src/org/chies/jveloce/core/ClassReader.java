package org.chies.jveloce.core;

import java.text.ParseException;

public class ClassReader {

    byte[] classData;
    // cursor for parsing the byte[]
    int cursor = 0;
    /* this array keeps track of entries in the constant pool
     * ie, the i-th element in this array records the start byte position of the
     * i-th entry in the constant pool. */
    int[] poolRefs;
    /* this array keeps track of the tag bits for entries in the constant pool
     * ie, the i-th element in this array records the tag bit of 
     * i-th entry in the constant pool. */
    byte[] constTags;
    char accessFlags;
    int classNameIndex; // index of CONSTANT_ClassInfo stuct rep. "this" class
    boolean debug = false;
    private int superNameIndex;
    
    public String getClassName(byte[] bytes) throws ParseException {
        this.classData = bytes;
        parse();
        return makeName(classNameIndex);
    }

    public String getSuperClassName(byte[] bytes) throws ParseException {
        this.classData = bytes;
        parse();
        return makeName(superNameIndex);
    }

    private String makeName(int entryIndex) {
        int loc = poolRefs[entryIndex]; // loc of entry in byte[]
        System.out.println("loc of entry " + loc);
        int nameLoc = getWordAt(loc + 1); // loc of UTF-8 struct rep. classname (in pool)
        System.out.println("loc in byte[] " + nameLoc);
        int index = poolRefs[nameLoc]; // loc of entry in byte[]
        int bufferSize = (int) getWordAt(index + 1);
        String name = new String(classData, index + 3, bufferSize);
        if (debug) {
            System.out.println("The name is the " + entryIndex + "th item in the Pool");
            System.out.println("location in byte[] is " + loc);
            System.out.println("tag at this point is " + constTags[entryIndex]);
            System.out.println("UTF-8 location " + nameLoc);
            System.out.println("tag at this point is " + constTags[nameLoc]);
            System.out.println("loc in byte[] " + index);
            System.out.println("length : " + bufferSize);
            System.out.println("Name = " + name);
        }
        return name.replaceAll("/", ".");
    }

    private void parse() throws ParseException {
        prepareForParse();
        readMagic();
        readVersion();
        parseConstPool(prepareForPool());
        readAccessBits();
        readClassNameIndex();
        readSuperNameIndex();
        // @todo rest to be implemented
    }

    private void readMagic() throws ParseException {
        int magic = getNextDWord();
        if (magic != 0xcafebabe) {
            if (debug) {
                System.out.println("Magic returned " + magic);
                System.out.println("Magic required " + 0xcafebabe);
                System.out.println("NOT CLASS FILE");
            }
            throw new ParseException("Error reading magic", cursor);
        } else {
            if (debug) {
                System.out.println("CLASS FILE..!!!");
            }
        }
    }

    private void readVersion() {
        int minor = getNextWord();
        int major = getNextWord();
        if (debug) {
            System.out.println(major);
            System.out.println(minor);
        }
    }

    private int prepareForPool() {
        int poolSize = getNextWord();
        if (debug) {
            System.out.println("Poolsize " + poolSize);
        }
        return poolSize;
    }

    private void parseConstPool(int poolSize) throws ParseException {
        poolRefs = new int[poolSize];
        constTags = new byte[poolSize];
        for (int count = 1; count < poolSize; count++) {
            byte readByte = classData[cursor++];
            constTags[count] = readByte;
            poolRefs[count] = cursor - 1;
            switch (readByte) {
                case 1: // UTF-8 data
                    int dataLength = getNextWord();
                    cursor += dataLength;
                    break;
                case 7: // class_Info stucture
                case 8: // CONSTANT_String 
                    cursor += 2;
                    break;
                case 3:  //CONSTANT_Integer 
                case 4:  //CONSTANT_Float 
                case 9:  //CONSTANT_Fieldref 
                case 10: //CONSTANT_Methodref 
                case 11: //CONSTANT_InterfaceMethodref 
                case 12:
                    cursor += 4;
                    break;
                case 5:
                case 6:
                    cursor += 8;
                    /* For 64-bit data, two entries in the cont.Pool are usd.
                        so, if nth entry is a double/long, then the next usable
                        entry is n+2 th entry.the n+1th entry should be valid,
                        but it is considered unusable. (- from sun's VM Spec)
                        so as far as parsing goes, we can safly skip the next entry.
                    */
                    count++;
                    break;
                default :
                    if (debug) {
                        System.out.println("FATAL : Error in parsing constant " +
                                           "pool.\n Error is Unrecognized tag-bit" + readByte + " was hit at " + (cursor
                                                                                                                  - 1));
                    }
                    throw new ParseException("FATAL : Error in parsing constant " +
                                             "pool.\n Error is Unrecognized tag-bit" + readByte + " was hit at " + (cursor
                                                                                                                    - 1), (cursor
                                                                                                                           - 1));
            }
        }
    }

    private void readAccessBits() {
        accessFlags = getNextWord();
        if (debug) {
            System.out.println("access Flags found at " + (cursor - 3));
        }
    }

    private void readClassNameIndex() {
        classNameIndex = getNextWord(); // index of CONSTANT_ClassInfo stuct rep. "this" class
        if (debug) {
            System.out.println("ClassName entry index found at " + (cursor - 3));
        }
    }

    private void readSuperNameIndex() {
        superNameIndex = getNextWord(); // index of CONSTANT_ClassInfo stuct rep. "this" class
        if (debug) {
            System.out.println("SuperClassName entry index found at " + (cursor - 3));
        }
    }

    private void prepareForParse() {
        classNameIndex = 0;
        constTags = new byte[0];
        poolRefs = new int[0];
        cursor = 0;
    }

    private int getNextDWord() {
        int end = cursor + 4;
        int var = 0;
        for (; cursor < end; cursor++) {
            var <<= 8;
            var |= classData[cursor] & 0xff;
        }
        return var;
    }

    private char getNextWord() {
        int end = cursor + 2;
        char var = 0;
        for (; cursor < end; cursor++) {
            var <<= 8;
            var |= classData[cursor] & 0xff;
        }
        return var;
    }

    private char getWordAt(int loc) {
        int end = loc + 2;
        char var = 0;
        for (; loc < end; loc++) {
            var <<= 8;
            var |= classData[loc] & 0xff;
        }
        return var;
    }

}
