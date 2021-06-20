import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import stev.kwikemart.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Laurent SAVIVANH - SAVL01039905
 * Vandy FATHI - FATV23119803
 * Hamza KARHAT - KARH02079707
 *
 *
 *
 *
 *
 *
* * * * * * * * * * * * * *  Classes :
* Valides:
* UPC {
* UPCV1 = (length == 11 ); (groupe)
* UPCV2 = (is not duplicated); (specifiaue)
* UPCV3 = (first char of upc is 2); (groupe)
* UPCV4 = (first char of upc is neither 2 or 5); (groupe)
* UPCV5 = (first char of upc is 5) (groupe)
* }
 *
 *
* Quantity :{
* QV1 = (0 < int <35), (intervalle)
* QV2 = (Decimals if UCPV3), (groupe ∩ specifique)
* QV3 = (integers <0 if UPCI2) (groupe ∩ specifique)
* }
 *
 *
* Retail_price :{
* RPV1 = (>=0 =<35), (intervalle)
* RPV2 = (>=0 =<10), (intervalle) Le deuxieme est pour les coupons, j'ai trouve les valeurs du coupon par tatonnement
* }
 *
 *
* Invalides:
* UPC :{
* UPCI1 = (length != 11 ), (groupe)
* UPCI2 = (duplicated), (specifique)
* }
 *
 *
* Quantity :{
* QI1 = (integers <0 if UPC does not already exists with Quantity > 0), (groupe ∩ specifique)
* QI2 =( Decimals if UPC.charAt(0) !=2) (groupe ∩ specifique)
* }
 *
 *
* Retail_price :
* * {
* RPI1 = (<=0), (intervalle)
* RPI2 = (=>35) (intervalle)
* }
*/
public class RegisterTest {
	List<Item> grocery;
	Register register;

	@BeforeClass
	public static void BeforeClass() throws Exception{
	}

	@Before
	public void setUp() throws Exception {
	 grocery = new ArrayList<>();
		register = Register.getRegister();
		register.changePaper(PaperRoll.LARGE_ROLL);


	}

	@After
	public void tearDown() throws Exception {
		grocery.clear();

	}

	/**
	 * Classes : UPCV1 ∩ UPCV2 ∩ UPCV4; QV1 ; RPV1
	 * Result : UPC 12th generated number is the right one
	 */
	@Test
	public void testUPCRightLength() {

		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 15));
		assertTrue((Upc.getCheckDigit("12345678901") ==  Character.getNumericValue( grocery.get(0).getUpc().charAt( grocery.get(0).getUpc().length()-1) )));
		System.out.println(register.print(grocery));

	}


	/**
	 * Classes : UPCV1 ∩ UPCV2 ∩ UPCV3; QV2; RPV1
	 * Result : UPC 12th generated number is the right one
	 */
	@Test
	public void testUPCRightLength2() {

		grocery.add(new Item(Upc.generateCode("22345678901"), "Bananas", 1.5, 15));
		assertTrue((Upc.getCheckDigit("22345678901") ==  Character.getNumericValue( grocery.get(0).getUpc().charAt( grocery.get(0).getUpc().length()-1) )));
		register.print(grocery);

	}



	/**
	 * Class : UPCI1, QV1, RPV1
	 * Result : InvalidUPCException
	 */
	@Test(expected = InvalidUpcException.UpcTooLongException.class)
	public void testUPCWrongLength() {

		grocery.add(new Item(Upc.generateCode("123456789012"), "Bananas", 1, .5));
		register.print(grocery);

	}


	/**
	 * Classe :  (UPCV1 ∩ UPCV2, QV1, RPV1) ∪ (UPCI2, QV1, RPV1)
	 * Result : DuplicatedItemException
	 */
	@Test(expected = Register.DuplicateItemException.class)
	public void testUPCDuplicated1() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 15));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 15));

		System.out.println(register.print(grocery));

	}
	/**
	 * Classe :  (UPCV1 ∩ UPCV2, QV1, RPV1) ∪ (UPCI2, QV3, RPV1) here q1 > -q2
	 * Result : total price printed is the right one
	 */
	@Test
	public void testUPCDuplicated2() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 2, 15));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 15));
		double total_price = grocery.get(0).getRetailPrice()*grocery.get(0).getQuantity() +grocery.get(1).getRetailPrice()*grocery.get(1).getQuantity();
		total_price *= 1.05;
		assertTrue(register.print(grocery).contains("TOTAL                               "+total_price));
	}
	/**
	 * Classe :  (UPCV1 ∩ UPCV2, QV1, RPV1) ∪ (UPCI2, QV3, RPV1) here q2 == - q1
	 * Result : total price printed is the right one
	 */
	@Test
	public void testUPCDuplicated3() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 15));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 15));
		double total_price = grocery.get(0).getRetailPrice()*grocery.get(0).getQuantity() +grocery.get(1).getRetailPrice()*grocery.get(1).getQuantity();
		total_price *= 1.05;
		assertTrue(register.print(grocery).contains("TOTAL                               "+total_price));
	}
	/**
	 * Classe :  Same as last one but this time i'm starting with the negative quantity (meaning you're not supposed to remove something you didnt already add)
	 * Result : NoSuchItemException
	 */
	@Test(expected = Register.NoSuchItemException.class)
	public void testUPCDuplicated4() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 15));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 15));
		register.print(grocery);

	}
	/**
	 * Classe :  (UPCV1 ∩ UPCV2, QV2, RPV1) ∪ (UPCI2, QV2 ∩ QV3, RPV1) here q2 == - q1
	 * Result : total price printed is the right one
	 */
	@Test
	public void testUPCDuplicated5() {

		grocery.add(new Item(Upc.generateCode("22345678901"), "Bananas", 1.5, 15));
		grocery.add(new Item(Upc.generateCode("22345678901"), "Bananas", -1.5, 15));
		double total_price = grocery.get(0).getRetailPrice()*grocery.get(0).getQuantity() +grocery.get(1).getRetailPrice()*grocery.get(1).getQuantity();
		total_price *= 1.05;
		assertTrue(register.print(grocery).contains("TOTAL                               "+total_price));


	}
	/**
	 * Classe :  (UPCV1 ∩ UPCV2, QV2, RPV1) ∪ (UPCI2, QV2 ∩ QV3, RPV1) here q2 == - q1
	 * Result : NoSuchItemException
	 */

	@Test(expected = Register.NoSuchItemException.class)
	public void testUPCDuplicated6() {
		grocery.add(new Item(Upc.generateCode("22345678901"), "Bananas", -1.5, 15));
		grocery.add(new Item(Upc.generateCode("22345678901"), "Bananas", 1.5, 15));
		register.print(grocery);
	}

	/**
	 * Classe :  (UPCV1 ∩ UPCV2, , QV1, RPV1) ∪ (UPCI2, QV3, RPV1) here q1 < -q2 // this test should not pass because this situation is not handled by the library
	 * Result : StringIndexOufOfBoundsException
	 */
	@Test(expected = StringIndexOutOfBoundsException.class)
	public void testUPCDuplicated7() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 2, 15));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -3, 15));
		register.print(grocery);

	}
	/**
	 * Classe : RPV2 ∩ UPCV5
	 * Result : Printed price is the right one
	 */
	@Test
	public void testRetailPriceCouponValid() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 2, 15));
		grocery.add(new Item(Upc.generateCode("52345678901"), "Bananas", 1, 8));
		double total_price = (grocery.get(0).getRetailPrice()*grocery.get(0).getQuantity())*1.05 - (grocery.get(1).getRetailPrice()*grocery.get(1).getQuantity());
		assertTrue(register.print(grocery).contains("TOTAL                               "+total_price));

	}
	/**
	 * Classe : RPV2 ∩ UPCV5
	 * Result : Printed price is the right one
	 */
	@Test
	public void testRetailPriceCouponValid2() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 2, 15));
		grocery.add(new Item(Upc.generateCode("52345678901"), "Bananas", 1, 8));
		double total_price = (grocery.get(0).getRetailPrice()*grocery.get(0).getQuantity())*1.05 - (grocery.get(1).getRetailPrice()*grocery.get(1).getQuantity());
		assertTrue(register.print(grocery).contains("TOTAL                               "+total_price));

	}
	/**
	 * Classe : RPV1 ∩ UPCV4
	 * Result : Printed price is the right one
	 */
	@Test
	public void testRetailPriceValid2() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 2, 15));
		grocery.add(new Item(Upc.generateCode("22345678901"), "Bananas", 1.5, 8));
		double total_price = grocery.get(0).getRetailPrice()*grocery.get(0).getQuantity() +grocery.get(1).getRetailPrice()*grocery.get(1).getQuantity();
		total_price *= 1.05;
		assertTrue(register.print(grocery).contains("TOTAL                               "+total_price));

	}
	/**
	 * Classe : RPV2 ∩ UPCV4
	 * Result : printed price is the right one
	 */
	@Test
	public void testRetailPriceValid3() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 5, 8));
		double total_price = grocery.get(0).getRetailPrice()*grocery.get(0).getQuantity();
		total_price *= 1.05;
		assertTrue(register.print(grocery).contains("TOTAL                               "+total_price));

	}



	/**
	 * Classe : QV2
	 * Result : printed price is the right one
	 */
	@Test
	public void testQuantityNegative() {
		grocery.add(new Item(Upc.generateCode("22345678901"), "Bananas", 1.5, 1.5));
		double total_price = grocery.get(0).getRetailPrice() * grocery.get(0).getQuantity();
		total_price = total_price*1.05;
		System.out.println(total_price);
		System.out.println(register.print(grocery));
		assertTrue(register.print(grocery).contains("TOTAL                               "+Math.round(total_price * 100.0) / 100.0));

	}
	/**
	 * Classe : QI1
	 * Result : NoSuchItemException
	 */
	@Test(expected = Register.NoSuchItemException.class)
	public void testQuantityDecimalUnvalid1() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5));

		register.print(grocery);

	}
	/**
	 * Classe : QI2
	 * Result : InvalidQuantityException
	 */
	@Test(expected = InvalidQuantityException.InvalidQuantityForCategoryException.class)
	public void testQuantityDecimalUnvalid() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1.5, 1.5));

		register.print(grocery);

	}

	/**
	 * Classe : RPI1
	 * Result : NegativeAmountException
	 */
	@Test(expected = AmountException.NegativeAmountException.class)
	public void testPriceNegative() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, -25));

		register.print(grocery);

	}

	/**
	 * Classe : RPI2
	 * Result : NegativeAmountException
	 */
	@Test(expected = AmountException.AmountTooLargeException.class)
	public void testPriceTooHigh() {
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 475));

		register.print(grocery);

	}



}
