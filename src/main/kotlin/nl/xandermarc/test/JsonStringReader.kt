/*
 * Copyright (c) 1996, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package nl.xandermarc.test

import java.io.IOException
import java.io.Reader
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * A character stream whose source is a string.
 *
 * @author      Mark Reinhold
 * @since       1.1
 */
class JsonStringReader(s: String) : Reader() {
    val length = s.length
    var str: String?
        private set
    var next = 0
        private set
    private var mark = 0

    /**
     * Creates a new string reader.
     *
     * @param s  String providing the character stream.
     */
    init {
        this.str = s
    }

    /** Check to make sure that the stream has not been closed  */
    @Throws(IOException::class)
    private fun ensureOpen() {
        if (str == null) throw IOException("Stream closed")
    }

    /**
     * Reads a single character.
     *
     * @return     The character read, or -1 if the end of the stream has been
     * reached
     *
     * @throws     IOException  If an I/O error occurs
     */
    @Throws(IOException::class)
    override fun read(): Int {
        synchronized(lock) {
            ensureOpen()
            if (next >= length) return -1
            return str!![next++].code
        }
    }

    /**
     * Reads characters into a portion of an array.
     *
     *
     *  If `len` is zero, then no characters are read and `0` is
     * returned; otherwise, there is an attempt to read at least one character.
     * If no character is available because the stream is at its end, the value
     * `-1` is returned; otherwise, at least one character is read and
     * stored into `cbuf`.
     *
     * @param      cbuf  {@inheritDoc}
     * @param      off   {@inheritDoc}
     * @param      len   {@inheritDoc}
     *
     * @return     {@inheritDoc}
     *
     * @throws     IndexOutOfBoundsException  {@inheritDoc}
     * @throws     IOException  {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        synchronized(lock) {
            ensureOpen()
            Objects.checkFromIndexSize(off, len, cbuf.size)
            if (len == 0) {
                return 0
            }
            if (next >= length) return -1
            val n = min((length - next).toDouble(), len.toDouble()).toInt()
            str!!.toCharArray(cbuf, off, next, next + n)
            next += n
            return n
        }
    }

    /**
     * Skips characters. If the stream is already at its end before this method
     * is invoked, then no characters are skipped and zero is returned.
     *
     *
     * The `n` parameter may be negative, even though the
     * `skip` method of the [Reader] superclass throws
     * an exception in this case. Negative values of `n` cause the
     * stream to skip backwards. Negative return values indicate a skip
     * backwards. It is not possible to skip backwards past the beginning of
     * the string.
     *
     *
     * If the entire string has been read or skipped, then this method has
     * no effect and always returns `0`.
     *
     * @param n {@inheritDoc}
     *
     * @return {@inheritDoc}
     *
     * @throws IOException {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        synchronized(lock) {
            ensureOpen()
            if (next >= length) return 0
            // Bound skip by beginning and end of the source
            var r = min((length - next).toDouble(), n.toDouble()).toLong()
            r = max(-next.toDouble(), r.toDouble()).toLong()
            next += r.toInt()
            return r
        }
    }

    /**
     * Tells whether this stream is ready to be read.
     *
     * @return True if the next read() is guaranteed not to block for input
     *
     * @throws     IOException  If the stream is closed
     */
    @Throws(IOException::class)
    override fun ready(): Boolean {
        synchronized(lock) {
            ensureOpen()
            return true
        }
    }

    /**
     * Tells whether this stream supports the mark() operation, which it does.
     */
    override fun markSupported(): Boolean {
        return true
    }

    /**
     * Marks the present position in the stream.  Subsequent calls to reset()
     * will reposition the stream to this point.
     *
     * @param  readAheadLimit  Limit on the number of characters that may be
     * read while still preserving the mark.  Because
     * the stream's input comes from a string, there
     * is no actual limit, so this argument must not
     * be negative, but is otherwise ignored.
     *
     * @throws     IllegalArgumentException  If `readAheadLimit < 0`
     * @throws     IOException  If an I/O error occurs
     */
    @Throws(IOException::class)
    override fun mark(readAheadLimit: Int) {
        require(readAheadLimit >= 0) { "Read-ahead limit < 0" }
        synchronized(lock) {
            ensureOpen()
            mark = next
        }
    }

    /**
     * Resets the stream to the most recent mark, or to the beginning of the
     * string if it has never been marked.
     *
     * @throws     IOException  If an I/O error occurs
     */
    @Throws(IOException::class)
    override fun reset() {
        synchronized(lock) {
            ensureOpen()
            next = mark
        }
    }

    /**
     * Closes the stream and releases any system resources associated with
     * it. Once the stream has been closed, further read(),
     * ready(), mark(), or reset() invocations will throw an IOException.
     * Closing a previously closed stream has no effect. This method will block
     * while there is another thread blocking on the reader.
     */
    override fun close() {
        synchronized(lock) {
            str = null
        }
    }

    fun reverse(): JsonStringReader {
        next -= 1
        return this
    }

    override fun toString(): String {
        return "JsonReader(pos=${next}, char='${str?.get(next)}')"
    }
}
