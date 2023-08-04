import React, {Component} from 'react';
import {StyleSheet,View,Pressable,Text,TouchableOpacity,ImageBackground, Switch} from 'react-native';
var previousPressIn;
var touchMode;
class SwitchView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setSwitch,
            position : this.props.position
        };
    };
    onPressInListener = (e) => {
        console.log(e.nativeEvent.locationX);
        console.log(e.nativeEvent.locationY);
        console.log(Date.now()," : ","onPressInListener of Switch");
        previousPressIn = e;
        touchMode = 0;
        // this.props.onPressInListener(e);
    }
    onLongPressListener = (e) => {
        console.log(Date.now()," : ","onLongPressListener of Switch");
        touchMode = 1;
    }
    onToggleListener = () => {
        this.props.onToggleListener(this.state.thisData.ID,this.state.thisData.isChecked);
    }
    onPressOutListener = (e) => {
        console.log(Date.now()," : ","onPressOutListener of Switch");

        console.log("out: ",e.nativeEvent.locationX);
        console.log("out: ",e.nativeEvent.locationY);
        if(touchMode == 0)
        {
            this.props.onPressInListener(e);
            this.props.onPressOutListener(e);
        }
        else if(touchMode == 1)
        {
            this.props.setLayoutTouchMode(1,e.target._internalFiberInstanceHandleDEV.memoizedProps.id);
        }
        
    }
    render() {
        console.log(Date.now()," : ","Switch Component created");
        console.log("FLUID(EXP) react: Switch Component created : ",global.nativePerformanceNow());
        var image = this.state.thisData.Image;
        if(this.state.position=="coordinate") {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y,height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    <TouchableOpacity style={{ height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                    id={this.state.thisData.ID}
                    onPressIn={this.onPressInListener}
                    onLongPress={this.onLongPressListener}
                    onPressOut={this.onPressOutListener}>
                        <Text>{this.state.thisData.Text}</Text>
                        <Switch style={{ height: this.state.thisData.Height, width: this.state.thisData.Width-30, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                                id={this.state.thisData.ID}
                                trackColor={{false: '#767577', true: '#81b0ff'}}
                                thumbColor={this.state.thisData.isChecked ? '#f5dd4b' : '#f4f3f4'}
                                value={this.state.thisData.isChecked==0?false:true}
                                onValueChange={this.onToggleListener}
                                />
                    </TouchableOpacity>
                    
                </View>
            )
        }
        else if(this.state.position=="automatic") {
            return (
                <View key={this.state.thisData.ID} style={{height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    

                    <TouchableOpacity style={{flexDirection: 'row', height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                    id={this.state.thisData.ID}
                    onPressIn={this.onPressInListener}
                    onLongPress={this.onLongPressListener}
                    onPressOut={this.onPressOutListener}>
                        <Text>{this.state.thisData.Text}</Text>
                        <Switch style={{ height: this.state.thisData.Height, width: this.state.thisData.Width-30, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                                id={this.state.thisData.ID}
                                trackColor={{false: '#767577', true: '#81b0ff'}}
                                thumbColor={this.state.thisData.isChecked ? '#f5dd4b' : '#f4f3f4'}
                                value={this.state.thisData.isChecked==0?false:true}
                                onValueChange={this.onToggleListener}
                                />
                    </TouchableOpacity>

                </View>
            )
        }
        
    };
}

export default SwitchView;
