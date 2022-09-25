import React, {Component} from 'react';
import {StyleSheet,View, Image, Pressable} from 'react-native';

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
    onPressInListener = (e) => {
        console.log("onPressInListener of ImageView");
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        console.log("onPressOutListener of ImageView");
        this.props.onPressOutListener(e);
    }
    render() {
        console.log("ImageView Component");
        var bitmap = this.state.thisData.Bitmap;
        if(this.state.position==="cordinate")
        {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",height: this.state.thisData.Height, width: this.state.thisData.Width,left: this.state.thisData.X, top: this.state.thisData.Y, alignItems:'flex-start', borderBottonWidth : StyleSheet.hairlineWidth}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", }}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                        <Image
                            id={this.state.thisData.ID}
                            source={{
                                uri: `data:image/png;base64,${bitmap}`,
                                }} style={{alignItems:'center', height: this.state.thisData.Height, width: this.state.thisData.Width}} resizeMode={'contain'}/>
                    </Pressable>
                </View>
            )
        }
        else if(this.state.position==="automatic")
        {
            return (
                <View key={this.state.thisData.ID} style={{alignItems:'flex-start',height: this.state.thisData.Height, width: this.state.thisData.Width, borderBottonWidth : StyleSheet.hairlineWidth}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", }}
                        id={this.state.thisData.ID}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                        <Image
                            id={this.state.thisData.ID}
                            source={{
                                uri: `data:image/jpeg;base64,${bitmap}`,
                                }} style={{alignItems:'center', height: this.state.thisData.Height, width: this.state.thisData.Width}} resizeMode={'contain'}/>
                    </Pressable>
                </View>
            )
        }
    };
}

export default ImageView