import React, {Component} from 'react';
import {StyleSheet,View,Pressable,Text,TouchableOpacity,ImageBackground} from 'react-native';
import { RadioButton } from 'react-native-paper';
var previousPressIn;
var touchMode;
class RadioGroupView extends Component{
    constructor(props){
        super(props);
        this.state = {
            thisData : this.props.setRadioGroup,
            position : this.props.position
        };
    };
    onPressInListener = (e) => {
        console.log(e.nativeEvent.locationX);
        console.log(e.nativeEvent.locationY);
        console.log(Date.now()," : ","onPressInListener of RadioGroup");
        previousPressIn = e;
        touchMode = 0;
        // this.props.onPressInListener(e);
    }
    onLongPressListener = (e) => {
        console.log(Date.now()," : ","onLongPressListener of RadioGroup");
        touchMode = 1;
    }
    onItemClickListener = (e) => {
        console.log(e);
        this.props.onItemClickListener(e);
    }
    onPressOutListener = (e) => {
        console.log(Date.now()," : ","onPressOutListener of RadioGroup");

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
        console.log(Date.now()," : ","RadioGroup Component created");
        console.log("FLUID(EXP) react: RadioGroup Component created : ",global.nativePerformanceNow());
        var image = this.state.thisData.Image;

        let RadioButtons = this.state.thisData.ChildrenData.map((item,index)=>{
            if(this.state.thisData.CheckedChildID === item.ID)
            {
                return (
                    <View key={item.ID} style={{flexDirection:'row', alignContent: 'center',   justifyContent: 'space-evenly', alignItems: "center"}}>
                        <Pressable style={{ flexDirection:'row',alignContent: 'center',   justifyContent: 'center', alignItems: "center"}}
                        id={item.ID}
                        onPress={() => this.onItemClickListener(item.ID)}>
                            <RadioButton
                                style={{flex : 1}}
                                key = {item.ID}
                                id = {item.ID}
                                value={item.Text}
                                status={'checked'}
                                onPress={() => this.onItemClickListener(item.ID)}
                            />
                            <Text style={{fontSize : 40, flex : 5}}>{item.Text}</Text>
                        </Pressable>
                    </View>
                )
            }
            else
            {
                return (
                    <View key={item.ID} style={{flexDirection:'row', alignContent: 'center',   justifyContent: 'space-evenly', alignItems: "center"}}>
                        <Pressable style={{ flexDirection:'row',alignContent: 'center',   justifyContent: 'center', alignItems: "center"}}
                        id={item.ID}
                        onPress={() => this.onItemClickListener(item.ID)}>
                            <RadioButton
                                style={{flex : 1}}
                                key = {item.ID}
                                id = {item.ID}
                                value={item.Text}
                                status={'unchecked'}
                                onPress={() => this.onItemClickListener(item.ID)}
                            />
                            <Text style={{fontSize : 40, flex : 5}}>{item.Text}</Text>
                        </Pressable>
                    </View>
                )
            }
        })
        if(this.state.position=="coordinate") {
            return (
                <View key={this.state.thisData.ID} style={{position: "absolute",left: this.state.thisData.X, top: this.state.thisData.Y,height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'white',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    {RadioButtons}
                    
                </View>
            )
        }
        else if(this.state.position=="automatic") {
            return (
                <View key={this.state.thisData.ID} style={{height: this.state.thisData.Height, width: this.state.thisData.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'white',borderBottomWidth: StyleSheet.hairlineWidth}}>
                    {RadioButtons}

                </View>
            )
        }
        
    };
}

export default RadioGroupView;
