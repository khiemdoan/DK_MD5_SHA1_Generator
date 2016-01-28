
public class SHA1 {

    public SHA1() {
    }

    private static final int INIT_A = 0x67452301;
    private static final int INIT_B = 0xEFCDAB89;
    private static final int INIT_C = 0x98BADCFE;
    private static final int INIT_D = 0x10325476;
    private static final int INIT_E = 0xC3D2E1F0;

    // tính giá trị hàm băm ra hex string
    public String generateToString(String data) {
        byte[] bytes = generate(data.getBytes());
        String str = toHexString(bytes);
        return str;
    }

    // tính giá trị hàm băm ra chuỗi các byte
    public byte[] generate(String data) {
        byte[] bytes = generate(data.getBytes());
        return bytes;
    }

    // tính giá trị hàm băm ra hex string
    public String generateToString(byte[] data) {
        byte[] bytes = generate(data);
        String str = toHexString(bytes);
        return str;
    }

    // tính giá trị hàm băm ra chuỗi các byte
    public byte[] generate(byte[] data) {
        // nhồi dữ liệu
//        data = padTheData(data);

        int dataLengthBytes = data.length;      // kích thước dữ liệu đã nhồi

        // kích thước dữ liệu + 8 byte cuối biểu diễn độ dài dữ liệu
        // chia 64 để lấy số blocks 64-bytes chưa nhồi dữ liệu
        // cộng 1 để luôn nhồi dữ liệu
        // kích thước chia hết cho block 64-bytes vẫn nhồi 1 block = 512 bits
        int numBlocks = ((dataLengthBytes + 8) >>> 6) + 1;

        // tổng kích thước sau khi đã nhồi dữ liệu
        int totalLength = numBlocks << 6;   // numBlocks * 64

        // khai báo mảng để nhồi dữ liệu
        byte[] paddingBytes = new byte[totalLength - dataLengthBytes];

        paddingBytes[0] = (byte) 0x80;  // byte đầu tiên được nhồi là 1000 0000

        long dataLenBits = (long) dataLengthBytes << 3;  // chuyển số byte thành số bit

        for (int i = 0; i < 8; i++) {
            // thuật toán sử dụng big endian
            paddingBytes[paddingBytes.length - 1 - i] = (byte) dataLenBits;
            dataLenBits >>>= 8;
        }

        // khởi tạo giá trị cho bộ đệm 160-bit
        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;
        int e = INIT_E;

        // xử lý từng khối 512-bit
        for (int i = 0; i < numBlocks; i++) {
            // lưu giá trị của lần tính trước
            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;
            int originalE = e;

            int index = i << 6;     // biến trỏ tới vị trí trong dữ liệu

            int[] w = new int[80];

            // lấy dữ liệu cho 16 block 32-bit
            for (int j = 0; j < 64; j++, index++) {
                int temp;
                if (index < dataLengthBytes) {
                    temp = data[index] & 0x000000FF;
                } else {
                    temp = paddingBytes[index - dataLengthBytes] & 0x000000FF;
                }
                w[j >>> 2] <<= 8;                       // sha1 dùng big-endian
                w[j >>> 2] |= temp;
            }

            for (int j = 16; j < 80; j++) {
                int temp;
                temp = w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16];
                w[j] = Integer.rotateLeft(temp, 1);
            }

            int f = 0;
            // vòng lặp 1
            for (int j = 0; j < 20; j++) {
                f = (b & c) | ((~b) & d);
                int K = 0x5A827999;
                int temp = f + e;
                temp += Integer.rotateLeft(a, 5);
                temp += w[j];
                temp += K;
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = temp;
            }

            // vòng lặp 2
            for (int j = 20; j < 40; j++) {
                f = b ^ c ^ d;
                int K = 0x6ED9EBA1;
                int temp = f + e;
                temp += Integer.rotateLeft(a, 5);
                temp += w[j];
                temp += K;
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = temp;
            }

            // vòng lặp 3
            for (int j = 40; j < 60; j++) {
                f = (b & c) | (b & d) | (c & d);
                int K = 0x8F1BBCDC;
                int temp = f + e;
                temp += Integer.rotateLeft(a, 5);
                temp += w[j];
                temp += K;
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = temp;
            }

            // vòng lặp 4
            for (int j = 60; j < 80; j++) {
                f = b ^ c ^ d;
                int K = 0xCA62C1D6;
                int temp = f + e;
                temp += Integer.rotateLeft(a, 5);
                temp += w[j];
                temp += K;
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = temp;
            }

            // cộng kết quả cũ với kết quả mới sau 4 vòng xử lý
            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
            e += originalE;
        }

        // trả về kết quả
        byte[] sha1 = new byte[20];
        int count = 0;
        for (int i = 0; i < 5; i++) {
            int n = 0;
            if (i == 0) {
                n = a;
            } else if (i == 1) {
                n = b;
            } else if (i == 2) {
                n = c;
            } else if (i == 3) {
                n = d;
            } else {
                n = e;
            }
            for (int j = 0; j < 4; j++) {
                sha1[count++] = (byte) (n >>> (24 - (8 * j)));
            }
        }
        return sha1;
    }

    public String toHexString(byte[] b) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String s = String.format("%02X", b[i]);     // chuyen ky tu b[i] thanh hex roi luu vao chuoi s
            str.append(s);          // them chuoi s vao cuoi chuoi str
        }
        return str.toString();
    }
}
