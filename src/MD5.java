
public class MD5 {

    public MD5() {
    }

    private static final int INIT_A = 0x67452301;
    private static final int INIT_B = 0xEFCDAB89;
    private static final int INIT_C = 0x98BADCFE;
    private static final int INIT_D = 0x10325476;
    private static final int[] SHIFT_AMOUNTS = {
        7, 12, 17, 22,
        5, 9, 14, 20,
        4, 11, 16, 23,
        6, 10, 15, 21
    };
    private static final int[] TABLE_T = new int[64];

    static {
        for (int i = 0; i < 64; i++) {
            // TABLE_T[i] = 2^32 * abs( sin(i))
            TABLE_T[i] = (int) (long) ((0x1L << 32) * Math.abs(Math.sin(i + 1)));
        }
    }

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
    static public byte[] generate(byte[] data) {

        // lấy kích thước dữ liệu
        int dataLengthBytes = data.length;

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

        // gán giá trị cho 8 byte cuối cùng chứa kích thước dữ liệu theo bits
        for (int i = 0; i < 8; i++) {
            // tất cả các giá trị trong thuật toán dùng little-endian
            paddingBytes[paddingBytes.length - 8 + i] = (byte) dataLenBits;
            dataLenBits >>>= 8; // dịch 8 bits sang phải
        }

        // khởi tạo giá trị ban đầu cho bộ đệm 128-bit
        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;

        // xử lý từng khối 512-bit
        for (int i = 0; i < numBlocks; i++) {
            // lưu giá trị của lần tính trước
            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;

            // xác định vị trí con trỏ trong toàn bộ dữ liệu
            int index = i << 6;

            // mỗi buffer lưu trữ 32-bit của block 512-bit
            int[] buffer = new int[16];
            for (int j = 0; j < 64; j++, index++) {
                int temp;       // biến trung gian lấy từng byte của dữ liệu đầu vào
                if (index < dataLengthBytes) {
                    temp = data[index];
                } else {
                    temp = paddingBytes[index - dataLengthBytes];
                }
                temp <<= 24;                // md5 sử dụng little-endian
                buffer[j >>> 2] >>>= 8;
                buffer[j >>> 2] |= temp;
            }

            // 4 vòng, mỗi vòng 16 bước
            for (int j = 0; j < 64; j++) {
                int f = 0;
                int bufferIndex = 0;

                // phân trường hợp cho 4 vòng
                int div16 = j >>> 4;
                switch (div16) {
                    case 0:
                        f = (b & c) | (~b & d);             // F := (B and C) or ((not B) and D)
                        bufferIndex = j;                    // bufferIndex = j
                        break;
                    case 1:
                        f = (b & d) | (c & ~d);             // F := (D and B) or (C and (not D))
                        bufferIndex = (j * 5 + 1) & 0x0F;   // bufferIndex = (5×j + 1) mod 16
                        break;
                    case 2:
                        f = b ^ c ^ d;                      // F := B xor C xor D
                        bufferIndex = (j * 3 + 5) & 0x0F;   // bufferIndex = (3×j + 5) mod 16
                        break;
                    case 3:
                        f = c ^ (b | ~d);                   // F := C xor (B or (not D))
                        bufferIndex = (j * 7) & 0x0F;       // bufferIndex = (7×j) mod 16
                        break;
                }

                int temp = a + f + buffer[bufferIndex] + TABLE_T[j];
                int shiftAmountsIndex = (div16 << 2) | (j & 0x3);
                temp = Integer.rotateLeft(temp, SHIFT_AMOUNTS[shiftAmountsIndex]);
                a = d;
                d = c;
                c = b;
                b += temp;
            }

            // cộng kết quả cũ với kết quả mới sau 4 vòng xử lý
            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
        }

        // trả kết quả của hàm
        byte[] md5 = new byte[16];
        int count = 0;      // biến đếm trỏ từng phần tử của mảng md5
        for (int i = 0; i < 4; i++) {
            int n;          // biến trung gian
            if (i == 0) {
                n = a;
            } else if (i == 1) {
                n = b;
            } else if (i == 2) {
                n = c;
            } else {
                n = d;
            }
            for (int j = 0; j < 4; j++) {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return md5;
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
