unsigned int rotleft(unsigned int v, int c) {
    return v << c | v >> (32 - c);
}

unsigned int *sha1(unsigned char *in_data, unsigned long in_length) {

    unsigned int *hh;
    hh = malloc(4 * 5);

    hh[0] = 0x67452301;
    hh[1] = 0xEFCDAB89;
    hh[2] = 0x98BADCFE;
    hh[3] = 0x10325476;
    hh[4] = 0xC3D2E1F0;

    // copy data with appended stuff
    unsigned long ml_bytes;
    unsigned char *m;

    // append 1 byte 0x80, variable 0 padding, 8 byte bits, so that is a multiple of 64 bytes
    ml_bytes = in_length + 9;
    if (ml_bytes % 64 != 0) {
        ml_bytes = ml_bytes + 64 - (ml_bytes % 64);
    }

    m = malloc(ml_bytes);

    int i = 0;
    for (; i < in_length; i++) {
        m[i] = in_data[i];
    }

    // append 0x80 = 0b10000000
    m[i++] = 0x80;

    // append padding
    while (i < ml_bytes - 8) {
        m[i++] = 0;
    }

    // append length
    m[i++] = ((in_length * 8) & 0xFF00000000000000) >> 56;
    m[i++] = ((in_length * 8) & 0x00FF000000000000) >> 48;
    m[i++] = ((in_length * 8) & 0x0000FF0000000000) >> 40;
    m[i++] = ((in_length * 8) & 0x000000FF00000000) >> 32;
    m[i++] = ((in_length * 8) & 0x00000000FF000000) >> 24;
    m[i++] = ((in_length * 8) & 0x0000000000FF0000) >> 16;
    m[i++] = ((in_length * 8) & 0x000000000000FF00) >> 8;
    m[i++] = ((in_length * 8) & 0x00000000000000FF);

    unsigned int *w;
    w = malloc(4 * 80);

    for (int chunk_start = 0; chunk_start < ml_bytes; chunk_start += 64) {
        i = 0;
        for (; i < 16; i++) {
            unsigned int tmp;
            tmp = 0;
            tmp = tmp | (m[chunk_start + i * 4] << 24);
            tmp = tmp | (m[chunk_start + i * 4 + 1] << 16);
            tmp = tmp | (m[chunk_start + i * 4 + 2] << 8);
            tmp = tmp | (m[chunk_start + i * 4 + 3]);
            w[i] = tmp;
        }

        for (; i < 80; i++) {
            w[i] = rotleft((w[i-3] ^ w[i-8] ^ w[i-14] ^ w[i-16]), 1);
        }

        unsigned int a;
        unsigned int b;
        unsigned int c;
        unsigned int d;
        unsigned int e;

        a = hh[0];
        b = hh[1];
        c = hh[2];
        d = hh[3];
        e = hh[4];

        i = 0;
        for (; i < 80; i++) {
            unsigned int f;
            unsigned int k;
            if (i <= 19) {
                f = (b & c) | (~b & d);
                k = 0x5A827999;
            } else if (i <= 39) {
                f = b ^ c ^ d;
                k = 0x6ED9EBA1;
            } else if (i <= 59) {
                f = (b & c) | (b & d) | (c & d);
                k = 0x8F1BBCDC;
            } else {
                f = b ^ c ^ d;
                k = 0xCA62C1D6;
            }

            unsigned int temp;
            temp = rotleft(a, 5) + f + e + k + w[i];
            e = d;
            d = c;
            c = rotleft(b, 30);
            b = a;
            a = temp;
        }

        hh[0] = hh[0] + a;
        hh[1] = hh[1] + b;
        hh[2] = hh[2] + c;
        hh[3] = hh[3] + d;
        hh[4] = hh[4] + e;
    }

    free(w);
    free(m);

    return hh;
}
