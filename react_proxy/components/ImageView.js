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
        console.log(Date.now()," : ","this is dummy TextChangeListener of EditText");
    }
    onPressListener = (e) => {
        console.log("onPressListener of ImageView");
    }
    onPressInListener = (e) => {
        console.log(e.nativeEvent.locationX);
        console.log(e.nativeEvent.locationY);
        console.log(Date.now()," : ","onPressInListener of ImageView");
        this.props.onPressInListener(e);
    }
    onPressOutListener = (e) => {
        console.log(Date.now()," : ","onPressOutListener of ImageView");
        this.props.onPressOutListener(e);
    }
    render() {
        console.log(Date.now()," : ","ImageView Component created");
        var image = this.state.thisData.Image;
        if(this.state.position=="coordinate")
        {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",height: this.state.thisData.Height, width: this.state.thisData.Width,left: this.state.thisData.X, top: this.state.thisData.Y, alignItems:'flex-start', borderBottonWidth : StyleSheet.hairlineWidth}}>
                    <Pressable style={{alignContent: 'center',   justifyContent: 'center', alignItems: "center", }}
                        id={this.state.thisData.ID}
                        onPress={this.onPressListener}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                        <Image
                            id={this.state.thisData.ID}
                            source={{
                                uri: `data:image/png;base64,${image}`,
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
                        onPress={this.onPressListener}
                        onPressIn={this.onPressInListener}
                        onPressOut={this.onPressOutListener}>
                        <Image
                            id={this.state.thisData.ID}
                            source={{
                                uri: `data:image/jpeg;base64,${image}`,
                                }} style={{alignItems:'center', height: this.state.thisData.Height, width: this.state.thisData.Width}} resizeMode={'contain'}/>
                    </Pressable>
                </View>
            )
        }
    };
}

export default ImageView
