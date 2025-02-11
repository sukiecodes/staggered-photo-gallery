package com.example.cs501staggeredphotogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cs501staggeredphotogallery.ui.theme.CS501StaggeredPhotoGalleryTheme
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CS501StaggeredPhotoGalleryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StaggeredPhotoGallery(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Photo(val title: String, val fileName: String)  // photo data class definition

// will parse through the xml file to create a list of photo objects
fun loadPhotos(context: android.content.Context): List<Photo> {
    val photos = mutableListOf<Photo>()
    val parser = context.resources.getXml(R.xml.photos)

    var eventType = parser.eventType
    var title = ""
    var file = ""

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                if (parser.name == "title") {
                    title = parser.nextText()
                }
                if (parser.name == "file") {
                    file = parser.nextText()
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "photo") {
                    photos.add(Photo(title, file))
                }
            }
        }
        eventType = parser.next()
    }
    return photos
}

@Composable
fun StaggeredPhotoGallery(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val photos = remember { loadPhotos(context) }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2) // 2 columns with staggered heights
    ) {
        items(photos) { photo ->
            PhotoItem(photo = photo)
        }
    }
}

@Composable
fun PhotoItem(photo: Photo) {
    var isEnlarged by remember { mutableStateOf(false) }
    val originalSize = 300.dp
    val enlargedSize = 600.dp

    // animate the size change between original and enlarges
    val size by animateDpAsState(if (isEnlarged) enlargedSize else originalSize)

    // coroutine to handle animation
    val coroutineScope = rememberCoroutineScope()
    Image(
        painter = painterResource(id = getImageResourceId(photo.fileName)),
        contentDescription = photo.title,
        modifier = Modifier
            .size(size)
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    // toggle the size of the image on click
                    isEnlarged = !isEnlarged
                }
            }
    )
}

fun getImageResourceId(fileName: String): Int {
    return when (fileName) {
        "carnation.jpg" -> R.drawable.carnation
        "daisy.jpg" -> R.drawable.daisy
        "lavender.jpg" -> R.drawable.lavender
        "lily.jpg" -> R.drawable.lily
        "marigold.jpg" -> R.drawable.marigold
        "rose.jpg" -> R.drawable.rose
        "sunflower.jpg" -> R.drawable.sunflower
        else -> R.drawable.ic_launcher_foreground
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CS501StaggeredPhotoGalleryTheme {
        StaggeredPhotoGallery()
    }
}