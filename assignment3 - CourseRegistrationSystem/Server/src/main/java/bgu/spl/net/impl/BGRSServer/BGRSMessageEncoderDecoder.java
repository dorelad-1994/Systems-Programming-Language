package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGRSServer.messages.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSMessageEncoderDecoder implements MessageEncoderDecoder<Message> {

    private byte[] byteArr = new byte[1 << 10]; //start with 1k
    int limit;
    private short opcode;
    private int zeroCounter;


    @Override


    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        pushByte(nextByte);
        String[] messageFields = null;
        if (nextByte == '\0' & opcode != 0) {
            zeroCounter++;
        }
        if (limit == 2 & opcode == 0) {
            opcode = bytesToShort(byteArr);
        }
        if (opcode == 1 | opcode == 2 | opcode == 3) {// messages that have 2 strings
            if (zeroCounter == 2) {
                String s = new String(byteArr, 2, limit, StandardCharsets.UTF_8);// converts the byte array to string
                messageFields = s.split("\0");// splits the string into the two parts
                return createMessage(messageFields);
            }
        } else if (opcode == 4 | opcode == 11) {// empty messages
            return createMessage(messageFields);
        } else if (opcode == 5 | opcode == 6 | opcode == 7 | opcode == 9 | opcode == 10) {// messages that have 1 short
            if (limit == 4) {
                String s = new String(byteArr, 2, limit, StandardCharsets.UTF_8);
                messageFields = new String[1];
                messageFields[0] = s;
                return createMessage(messageFields);
            }
        } else if (opcode == 8) {// messages that have 1 string
            if (zeroCounter == 1) {
                String s = new String(byteArr, 2, limit - 3, StandardCharsets.UTF_8);
                messageFields = new String[1];
                messageFields[0] = s;
                return createMessage(messageFields);
            }
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        if (message instanceof ACK) {
            ACK ack = (ACK) message;
            String s = ack.getDataForClient() + "\0";
            byte[] opcodeArr = shortToBytes(ack.getOpcode());
            byte[] messageNumArr = shortToBytes(ack.getMessageOpcode());
            byte[] dataForClient = s.getBytes();
            byte [] output;
            if (dataForClient.length > 1) {
                output = new byte[opcodeArr.length + messageNumArr.length + dataForClient.length];
            } else {
                output = new byte[opcodeArr.length + messageNumArr.length];
            }
            int index = 0;
            for (byte b : opcodeArr) {
                output[index] = b;
                index++;
            }
            for (byte b : messageNumArr) {
                output[index] = b;
                index++;
            }
            if (dataForClient.length > 1) {
                for (byte b : dataForClient) {
                    output[index] = b;
                    index++;
                }
            }
            return output;
        } else {
            ERROR error = (ERROR) message;
            byte[] opcodeArr = shortToBytes(error.getOpcode());
            byte[] messageNumArr = shortToBytes(error.getMessageOpcode());
            byte[] output = new byte[opcodeArr.length + messageNumArr.length];
            int index = 0;
            for (byte b : opcodeArr) {
                output[index] = b;
                index++;
            }
            for (byte b : messageNumArr) {
                output[index] = b;
                index++;
            }
            return output;
        }
    }

    private Message createMessage(String[] messageFields) {
        Message m = null;
        if (opcode == 1)
            m = new ADMINREG(messageFields[0], messageFields[1]);
        if (opcode == 2)
            m = new STUDENTREG(messageFields[0], messageFields[1]);
        if (opcode == 3)
            m = new LOGIN(messageFields[0], messageFields[1]);
        if (opcode == 4)
            m = new LOGOUT();
        if (opcode == 5)
            m = new COURSEREG(bytesToShort(messageFields[0].getBytes()));
        if (opcode == 6)
            m = new KDAMCHECK(bytesToShort(messageFields[0].getBytes()));
        if (opcode == 7)
            m = new COURSESTAT(bytesToShort(messageFields[0].getBytes()));
        if (opcode == 8)
            m = new STUDENTSTAT(messageFields[0]);
        if (opcode == 9)
            m = new ISREGISTERED(bytesToShort(messageFields[0].getBytes()));
        if (opcode == 10)
            m = new UNREGISTER(bytesToShort(messageFields[0].getBytes()));
        if (opcode == 11)
            m = new MYCOURSES();
        clear();
        return m;
    }

    private void clear() {
        byteArr = new byte[1 << 10];
        limit = 0;
        opcode = 0;
        zeroCounter = 0;
    }

    private void pushByte(byte nextByte) {
        if (limit >= byteArr.length) {
            byteArr = Arrays.copyOf(byteArr, limit * 2);
        }
        byteArr[limit++] = nextByte;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }
}
