import React, {Component} from 'react';
import {StyleSheet,View, Image} from 'react-native';

class ImageView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setImageView,
            position : this.props.position
        };
    };
    TextChangeListener = (e) => {
        this.props.TextChangeListener(e);
        console.log("this is dummy TextChangeListener of EditText");
    }
    render() {
        console.log("ImageView Component");
        var bitmap = this.state.thisData.Bitmap;
        if(this.state.position==="cordinate")
        {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",height: this.state.thisData.Height, width: this.state.thisData.Width,left: this.state.thisData.X, top: this.state.thisData.Y, alignItems:'flex-start', borderBottonWidth : StyleSheet.hairlineWidth}}>
                    <Image
                        source={{
                            uri: `data:image/png;base64,${bitmap}`,
                            }} style={{alignItems:'center', height: this.state.thisData.Height, width: this.state.thisData.Width}} resizeMode={'contain'}/>
                </View>
            )
        }
        else if(this.state.position==="automatic")
        {
            return (
                <View key={this.state.thisData.ID} style={{alignItems:'flex-start',height: this.state.thisData.Height, width: this.state.thisData.Width, borderBottonWidth : StyleSheet.hairlineWidth}}>
                    <Image
                        source={{
                            uri: `data:image/png;base64,${bitmap}`,
                            }} style={{alignItems:'center', height: this.state.thisData.Height, width: this.state.thisData.Width}} resizeMode={'contain'}/>
                </View>
            )
        }
    };
}

export default ImageView