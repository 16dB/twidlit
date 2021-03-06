/**
 * Copyright 2015 Pushkar Piggott
 *
 * Twiddle.java
 */
 
package pkp.twiddle;

import java.util.ArrayList;
import java.net.URL;
import pkp.io.LineReader;
import pkp.io.Io;
import pkp.util.Pref;
import pkp.util.Log;

///////////////////////////////////////////////////////////////////////////////
public class Twiddle extends java.lang.Object {

   ////////////////////////////////////////////////////////////////////////////
   public static ArrayList<Twiddle> read(URL url) {
      LineReader lr = new LineReader(url, Io.sm_MUST_EXIST);
      if (lr == null) {
         return null;
      }
      ArrayList<Twiddle> twiddles = new ArrayList<Twiddle>();
      String line;
      for (int i = 1; (line = lr.readLine()) != null; ++i) {
         Twiddle tw = new Twiddle(line);
         if (!tw.isValid()) {
            Log.warn(String.format("Failed to read line %d \"%s\" of \"%s\"", i, line, url.getPath()));
         } else {
//System.out.println(tw);
            twiddles.add(tw);
         }
      }
      lr.close();
      return twiddles;
   }
   
   ////////////////////////////////////////////////////////////////////////////
   public static Twiddle fromChordValue(int twiddle) {
      return new Twiddle(Chord.fromChordValue(twiddle),
                         new ThumbKeys((twiddle >> 8) & ThumbKeys.sm_VALUES));
   }

   ////////////////////////////////////////////////////////////////////////////
   public static Twiddle fromChordValue(int chord, int thumbKeys) {
      return new Twiddle(Chord.fromChordValue(chord),
                         new ThumbKeys(thumbKeys & ThumbKeys.sm_VALUES));
   }

   ////////////////////////////////////////////////////////////////////////////
   public static Twiddle fromChordOrMouse(int i, int tk) {
      return new Twiddle((i > Chord.sm_VALUES)
                         ? Chord.fromMouseButton(i >> 8)
                         : Chord.fromChordValue(i),
                         new ThumbKeys(tk));
   }

   ////////////////////////////////////////////////////////////////////////////
   public Twiddle() {
      m_Chord = Chord.fromChordValue(0);
      m_ThumbKeys = new ThumbKeys(0);
   }

   ////////////////////////////////////////////////////////////////////////////
   public Twiddle(Chord chord, ThumbKeys thumbKeys) {
      m_Chord = chord;
      m_ThumbKeys = thumbKeys;
   }

   ////////////////////////////////////////////////////////////////////////////
   public Twiddle(Chord chord) {
      m_Chord = chord;
      m_ThumbKeys = new ThumbKeys(0);
   }

   ////////////////////////////////////////////////////////////////////////////
   public Twiddle(Twiddle tw, Modifiers m) {
      m_Chord = tw.getChord();
      m_ThumbKeys = new ThumbKeys(tw.getThumbKeys().toInt() | ThumbKeys.fromModifiers(m));
   }

   ////////////////////////////////////////////////////////////////////////////
   public Twiddle(String in) {
      m_Chord = new Chord(in);
      if (m_Chord.isValid()) {
         m_ThumbKeys = new ThumbKeys(0);
      } else {
         String str = in.trim();
         int split = Io.findFirstOf(str, Io.sm_WS);
         m_ThumbKeys = new ThumbKeys(str.substring(0, split));
         m_Chord = new Chord(str.substring(split));
         if (!m_ThumbKeys.isEmpty() && m_Chord.isMouseButton()) {
            Log.warn("Ignoring thumb keys with mouse button " + m_Chord);
            m_ThumbKeys = new ThumbKeys(0);
         }
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   public Twiddle createModified(Modifiers mod) {
		return new Twiddle(m_Chord, m_ThumbKeys.plus(mod));
   }

   /////////////////////////////////////////////////////////////////////////////
   public Twiddle reversed() {
      return new Twiddle(getChord().reversed(), getThumbKeys());
   }

   ////////////////////////////////////////////////////////////////////////////
   public boolean equals(Object o) {
      Twiddle other = (Twiddle)o;
      return other != null
          && other.m_Chord.equals(m_Chord)
          && other.m_ThumbKeys.equals(m_ThumbKeys);
   }

   ////////////////////////////////////////////////////////////////////////////
   // a twiddle is less than another if it has fewer thumb buttons or
   // a smaller chord or a lesser thumb button.
   public boolean lessThan(Twiddle other) {
      int n = getThumbKeys().getCount();
      int on = other.getThumbKeys().getCount();
      if (n < on) {
         return true;
      }
      if (n > on) {
         return false;
      }
      int c = getChord().compare(other.getChord());
      if (c < 0) {
         return true;
      }
      if (c > 0) {
         return false;
      }
      return getThumbKeys().toInt() < other.getThumbKeys().toInt();
   }

   ////////////////////////////////////////////////////////////////////////////
   public KeyPressList getKeyPressList(KeyMap map) {
      if (getThumbKeys().isEmpty()) {
         // no modifiers, no options
         return map.getKeyPressList(this);
      } else {
         // try stripping off modifiers
         final Modifiers mods[] = Modifiers.getCombinations(getThumbKeys().toModifiers());
         for (int i = 0; i < mods.length; ++i) {
//System.out.println("Twiddle getKeyPressList " + mods[i].toString());
            Twiddle tw = new Twiddle(getChord(), getThumbKeys().minus(mods[i]));
            KeyPressList kpl = map.getKeyPressList(tw);
            if (kpl != null) {
               return kpl.createModified(mods[i]);
            }
         }
      }
      return null;
   }

   ////////////////////////////////////////////////////////////////////////////
   public String toShortString() {
      return (m_ThumbKeys.isEmpty() 
              ? ""
              : m_ThumbKeys.toString())
           + m_Chord.toString();
   }

   ////////////////////////////////////////////////////////////////////////////
   public boolean isValid() {
      return m_Chord != null
          && m_Chord.isValid()
          && m_ThumbKeys != null
          && m_ThumbKeys.isValid()
          && (m_Chord.isChord() || m_ThumbKeys.isEmpty()); 
   }

   ////////////////////////////////////////////////////////////////////////////
   public String toString() {
      return m_ThumbKeys.toString() 
           + (m_Chord.isMouseButton() ? " " : "  ")
           + m_Chord.toString();
   }

   ////////////////////////////////////////////////////////////////////////////
   public Chord getChord() { return m_Chord; }
   public ThumbKeys getThumbKeys() { return m_ThumbKeys; }
   public int toInt() { return (m_ThumbKeys.toInt() << 8) + m_Chord.toInt(); }
   public int toCfg() { return m_ThumbKeys.toCfg() | m_Chord.toCfg(); }

   // Data ////////////////////////////////////////////////////////////////////
   private Chord m_Chord;
   private ThumbKeys m_ThumbKeys;

   // Main ////////////////////////////////////////////////////////////////////
   public static void main(String[] args) {
      for (String arg: args) {
         Twiddle keys = new Twiddle(arg);
         System.out.printf("%s\n", keys.toString());
      }
   }
}
