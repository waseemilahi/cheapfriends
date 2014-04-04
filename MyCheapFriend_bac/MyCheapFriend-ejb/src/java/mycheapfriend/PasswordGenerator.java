/*
 * random password generator
 * code gotton from http://www.glenmccl.com/tip_010.htm
 * and modified for personal use
 */

package mycheapfriend;

/**
 *
 * @author Waseem Ilahi
 */
public class PasswordGenerator {

          public static String generatePassword(int lo, int hi){
                  int n = rand(lo, hi);
                  byte b[] = new byte[n];
                  for (int i = 0; i < n; i++){
                          b[i++] = (byte)rand('a', 'z');
                          b[i] = (byte)rand('0' , '9');
                  }
                  return new String(b, 0);
          }

          private static int rand(int lo, int hi){
                      java.util.Random rn = new java.util.Random();
                  int n = hi - lo + 1;
                  int i = rn.nextInt() % n;
                  if (i < 0)
                          i = -i;
                  return lo + i;
          }

          public static String generatePassword(){
                  return generatePassword(6, 6);
          }

}
