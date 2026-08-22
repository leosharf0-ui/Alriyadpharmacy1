package com.alriyad.pharmacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF1565C0)
private val DarkBlue = Color(0xFF0D47A1)
private val LightBlue = Color(0xFFE3F2FD)
private val Bg = Color(0xFFF7FAFD)

 data class Product(val id:Int,val name:String,val ingredient:String,val category:String,val price:String,val description:String)
private val products = listOf(
    Product(1,"Panadol Extra","Paracetamol + Caffeine","الأدوية","45 ج.م","مسكن للألم وخافض للحرارة."),
    Product(2,"Augmentin 1g","Amoxicillin + Clavulanic Acid","الأدوية","180 ج.م","مضاد حيوي يُستخدم حسب وصف الطبيب."),
    Product(3,"Vitamin C 1000","Ascorbic Acid","الفيتامينات","120 ج.م","فيتامين سي فوّار."),
    Product(4,"CeraVe Moisturizer","Ceramides","العناية بالبشرة","650 ج.م","مرطب للبشرة الجافة."),
    Product(5,"Baby Shampoo","Gentle Formula","الأم والطفل","210 ج.م","شامبو لطيف للأطفال."),
    Product(6,"Digital Thermometer","Digital Sensor","المستلزمات الطبية","160 ج.م","ميزان حرارة رقمي سريع القراءة.")
)

class MainActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { App() } }
}

@Composable fun App(){
    var tab by remember { mutableStateOf(0) }
    var screen by remember { mutableStateOf("home") }
    var selected by remember { mutableStateOf<Product?>(null) }
    var query by remember { mutableStateOf("") }
    var cart by remember { mutableStateOf<List<Product>>(emptyList()) }
    val ctx = LocalContext.current
    fun call(number:String){ ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))) }
    fun whatsapp(text:String){ ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/20${"01040750032".drop(1)}?text=${Uri.encode(text)}"))) }
    MaterialTheme(colorScheme = lightColorScheme(primary=Blue, secondary=DarkBlue, background=Bg)) {
        Scaffold(bottomBar={ if(screen=="home") BottomNav(tab){ tab=it } }) { pad ->
            Box(Modifier.fillMaxSize().padding(pad).background(Bg)){
                when(screen){
                    "home" -> when(tab){
                        0 -> Home(query,{query=it},{screen="search"},{screen="product";selected=it},{cart=cart+it})
                        1 -> Categories({screen="search";query=it},{screen="product";selected=it})
                        2 -> SearchScreen(query,{query=it},{screen="product";selected=it})
                        else -> ContactScreen({call("01040750032")},{call("01035650029")},{whatsapp("مرحباً Alriyad Pharmacy، أريد الاستفسار عن منتج.")})
                    }
                    "search" -> SearchScreen(query,{query=it},{screen="product";selected=it})
                    "product" -> ProductScreen(selected!!,{cart=cart+it},{screen="home";tab=0})
                }
                if(cart.isNotEmpty() && screen=="home") FloatingCart(cart.size,{whatsapp("مرحباً Alriyad Pharmacy، أريد طلب: ${cart.joinToString { it.name }}")})
            }
        }
    }
}

@Composable fun Header(){
    Row(Modifier.fillMaxWidth().background(Blue).padding(horizontal=20.dp,vertical=18.dp),verticalAlignment=Alignment.CenterVertically){
        Box(Modifier.size(48.dp).clip(CircleShape).background(Color.White),contentAlignment=Alignment.Center){ Text("Rx",color=Blue,fontWeight=FontWeight.Bold,fontSize=20.sp) }
        Spacer(Modifier.width(12.dp)); Column{Text("Alriyad Pharmacy",color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold);Text("صيدلية الرياض",color=Color.White.copy(.85f),fontSize=13.sp)}
    }
}

@Composable fun Home(query:String,onQuery:(String)->Unit,onSearch:()->Unit,onProduct:(Product)->Unit,onAdd:(Product)->Unit){
    LazyColumn(Modifier.fillMaxSize()){ item{Header()}; item{
        Column(Modifier.padding(18.dp)){ Text("أهلاً بك 👋",fontSize=25.sp,fontWeight=FontWeight.Bold,color=DarkBlue); Text("كل احتياجاتك الصحية في مكان واحد",color=Color.Gray); Spacer(Modifier.height(16.dp));
            SearchBox(query,onQuery,onSearch); Spacer(Modifier.height(20.dp));
            Promo(); Spacer(Modifier.height(22.dp)); SectionTitle("الأقسام"); Spacer(Modifier.height(10.dp)); CategoryRow(); Spacer(Modifier.height(22.dp)); SectionTitle("منتجات مميزة"); Spacer(Modifier.height(10.dp))
        }
    }; items(products.take(4)){ProductCard(it,onProduct,onAdd)}; item{Spacer(Modifier.height(80.dp))} }
}

@Composable fun SearchBox(value:String,onValue:(String)->Unit,onClick:()->Unit){ OutlinedTextField(value,onValue,Modifier.fillMaxWidth().clickable{onClick()},singleLine=true,placeholder={Text("ابحث عن دواء أو منتج")},leadingIcon={Icon(Icons.Default.Search,null)},shape=RoundedCornerShape(18.dp)) }
@Composable fun Promo(){ Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Blue),shape=RoundedCornerShape(22.dp)){ Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){ Column(Modifier.weight(1f)){Text("Alriyad Pharmacy",color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold);Text("رعاية أقرب إليك",color=Color.White.copy(.9f));Text("اطلب بسهولة عبر واتساب",color=Color.White.copy(.9f))};Icon(Icons.Default.LocalPharmacy,null,tint=Color.White,modifier=Modifier.size(64.dp)) } } }
@Composable fun SectionTitle(t:String){Text(t,fontSize=20.sp,fontWeight=FontWeight.Bold,color=DarkBlue)}
@Composable fun CategoryRow(){ LazyRow(horizontalArrangement=Arrangement.spacedBy(10.dp)){ items(listOf("الأدوية" to Icons.Default.Medication,"الفيتامينات" to Icons.Default.HealthAndSafety,"العناية بالبشرة" to Icons.Default.Face,"الأم والطفل" to Icons.Default.ChildCare,"مستلزمات طبية" to Icons.Default.MedicalServices)){ (name,icon)->Card(Modifier.width(110.dp),shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally){Icon(icon,null,tint=Blue,modifier=Modifier.size(32.dp));Spacer(Modifier.height(6.dp));Text(name,fontSize=12.sp,textAlign=TextAlign.Center)}} } } }

@Composable fun ProductCard(p:Product,onClick:(Product)->Unit,onAdd:(Product)->Unit){ Card(Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=6.dp).clickable{onClick(p)},shape=RoundedCornerShape(18.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)).background(LightBlue),contentAlignment=Alignment.Center){Icon(Icons.Default.Medication,null,tint=Blue,modifier=Modifier.size(34.dp))};Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(p.name,fontWeight=FontWeight.Bold,fontSize=16.sp);Text(p.ingredient,color=Color.Gray,fontSize=12.sp);Text(p.price,color=Blue,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=5.dp))};IconButton(onClick={onAdd(p)}){Icon(Icons.Default.AddShoppingCart,null,tint=Blue)}}} }

@Composable fun Categories(onCategory:(String)->Unit,onProduct:(Product)->Unit){ Column{Header(); LazyColumn(Modifier.padding(18.dp)){item{SectionTitle("كل الأقسام");Spacer(Modifier.height(12.dp))};items(products.groupBy{it.category}.keys.toList()){cat->Card(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{onCategory(cat)},shape=RoundedCornerShape(18.dp)){Row(Modifier.padding(18.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Category,null,tint=Blue);Spacer(Modifier.width(12.dp));Text(cat,fontWeight=FontWeight.Bold);Spacer(Modifier.weight(1f));Icon(Icons.Default.ChevronRight,null)}}};item{Spacer(Modifier.height(70.dp))}} } }

@Composable fun SearchScreen(query:String,onQuery:(String)->Unit,onProduct:(Product)->Unit){ Column{TopBar("البحث") { }; Column(Modifier.padding(18.dp)){SearchBox(query,onQuery,{});Spacer(Modifier.height(14.dp));val list=products.filter{it.name.contains(query,true)||it.ingredient.contains(query,true)||it.category.contains(query,true)};LazyColumn{items(list){ProductCard(it,onProduct,{})}}}} }
@Composable fun ProductScreen(p:Product,onAdd:(Product)->Unit,onBack:()->Unit){ Column{TopBar("تفاصيل المنتج",onBack);Column(Modifier.padding(20.dp)){Box(Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(24.dp)).background(LightBlue),contentAlignment=Alignment.Center){Icon(Icons.Default.Medication,null,tint=Blue,modifier=Modifier.size(110.dp))};Spacer(Modifier.height(20.dp));Text(p.name,fontSize=27.sp,fontWeight=FontWeight.Bold,color=DarkBlue);Text(p.ingredient,color=Color.Gray);Text(p.price,color=Blue,fontSize=21.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(vertical=12.dp));Text(p.description,fontSize=16.sp);Spacer(Modifier.height(24.dp));Button({onAdd(p)},Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp)){Icon(Icons.Default.AddShoppingCart,null);Spacer(Modifier.width(8.dp));Text("أضف للطلب")};Spacer(Modifier.height(10.dp));Text("تنبيه: الأدوية الموصوفة تُصرف وفق وصفة الطبيب.",color=Color.Gray,fontSize=12.sp)}} }
@Composable fun TopBar(title:String,onBack:(()->Unit)?=null){Row(Modifier.fillMaxWidth().background(Blue).padding(10.dp),verticalAlignment=Alignment.CenterVertically){if(onBack!=null)IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,null,tint=Color.White)};Text(title,color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(8.dp))}}

@Composable fun ContactScreen(call1:()->Unit,call2:()->Unit,wa:()->Unit){ Column{Header();LazyColumn(Modifier.padding(18.dp)){item{Text("تواصل معنا",fontSize=28.sp,fontWeight=FontWeight.Bold,color=DarkBlue);Text("صيدلية الرياض - مركز الرياض أسفل المسجد الكبير",color=Color.Gray,modifier=Modifier.padding(vertical=8.dp));Spacer(Modifier.height(15.dp))};item{ContactButton("01040750032","اتصال بالرقم الرئيسي",Icons.Default.Phone,call1)};item{ContactButton("01035650029","اتصال بالرقم الثاني",Icons.Default.Phone,call2)};item{ContactButton("واتساب 01040750032","تواصل عبر واتساب",Icons.Default.Chat,wa)}item{Card(Modifier.fillMaxWidth().padding(top=12.dp),shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=LightBlue)){Row(Modifier.padding(18.dp)){Icon(Icons.Default.LocationOn,null,tint=Blue);Spacer(Modifier.width(12.dp));Column{Text("العنوان",fontWeight=FontWeight.Bold,color=DarkBlue);Text("مركز الرياض – أسفل المسجد الكبير")}}}};item{Spacer(Modifier.height(30.dp));Text("لا يوجد خط ساخن",color=Color.Gray,fontSize=12.sp)}}} }
@Composable fun ContactButton(title:String,sub:String,icon:androidx.compose.ui.graphics.vector.ImageVector,action:()->Unit){Card(Modifier.fillMaxWidth().padding(vertical=6.dp).clickable{action()},shape=RoundedCornerShape(18.dp)){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,tint=Blue,modifier=Modifier.size(28.dp));Spacer(Modifier.width(12.dp));Column{Text(title,fontWeight=FontWeight.Bold);Text(sub,color=Color.Gray,fontSize=12.sp)}}}}
@Composable
fun FloatingCart(count: Int, action: () -> Unit) {
    FloatingActionButton(
        onClick = action,
        containerColor = Blue,
        contentColor = Color.White,
        modifier = Modifier.padding(18.dp)
    ) {
        BadgedBox(
            badge = {
                Badge {
                    Text(count.toString())
                }
            }
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
        }
    }
}
}
